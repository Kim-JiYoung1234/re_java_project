package com.human.ofim.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.EducationDto;
import com.human.ofim.dto.ExperienceDto;
import com.human.ofim.dto.ExpertInfoDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto; // [추가] 전문가 프로필 조회용
import com.human.ofim.dto.MemberDto;     // [추가] 회원 기본 정보 조회용
import com.human.ofim.mapper.ExpertUserMapper;
import com.human.ofim.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class ExpertService {

	@Autowired
    private MemberMapper memberMapper;
    private final ExpertUserMapper expertUserMapper;
 // 물리적 저장 경로 (본인 PC 환경에 맞게 수정: 예 C:/ofim/uploads/)
    private final String uploadPath = "D:/ofim_uploads/expert_docs/profiles/";
    
    // 🌟 [핵심 방어막] 리스트가 비어있거나 인덱스 범위를 초과하면 에러 대신 null 반환
    private <T> T safeGet(List<T> list, int index) {
        if (list != null && list.size() > index) {
            return list.get(index);
        }
        return null;
    }
    
 

    // 전문가 프로필 및 이미지 수정
    public void updateExpertProfile(ExpertUserDto expertDto, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                // 중복 방지를 위한 UUID 파일명 생성
                String originalName = file.getOriginalFilename();
                String ext = originalName.substring(originalName.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + ext;

                // 폴더 생성 및 물리적 저장
                File dir = new File(uploadPath);
                if (!dir.exists()) dir.mkdirs();
                
                File saveFile = new File(uploadPath, fileName);
                file.transferTo(saveFile);

                // DB에는 웹에서 접근 가능한 가상 경로 저장
                expertDto.setProfileImagePath("/uploads/expert_docs/profiles/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        expertUserMapper.updateExpertProfile(expertDto);
    }

    // ==========================================
    // 저장(INSERT) 로직
    // ==========================================
    @Transactional
    public void saveExpertAdditionalInfo(ExpertInfoDto info, Long memberId) {
        // ... (회원님이 작성하신 1.자격증, 2.학력, 3.경력, 4.정산 저장 로직 그대로 유지) ...
        
        // 1. 자격증 정보 저장
        if (info.getCertName() != null && !info.getCertName().isEmpty()) {
            for (int i = 0; i < info.getCertName().size(); i++) {
                if (info.getCertName().get(i) != null && !info.getCertName().get(i).trim().isEmpty()) {
                    CertificateDto certDto = new CertificateDto();
                    certDto.setMemberId(memberId);
                    certDto.setCertName(info.getCertName().get(i));
                    certDto.setIssuer(safeGet(info.getIssuer(), i));
                    certDto.setCertNo(safeGet(info.getCertNo(), i));
                    certDto.setIssuedDate(safeGet(info.getIssuedDate(), i));
                    certDto.setExpiredDate(safeGet(info.getExpiredDate(), i));
                    expertUserMapper.insertCertificate(certDto);
                }
            }
        }

        // 2. 학력 정보 저장
        if (info.getExpertSchool() != null && !info.getExpertSchool().isEmpty()) {
            for (int i = 0; i < info.getExpertSchool().size(); i++) {
                if (info.getExpertSchool().get(i) != null && !info.getExpertSchool().get(i).trim().isEmpty()) {
                    EducationDto eduDto = new EducationDto();
                    eduDto.setMemberId(memberId);
                    eduDto.setExpertSchool(info.getExpertSchool().get(i));
                    eduDto.setExpertMajor(safeGet(info.getExpertMajor(), i));
                    eduDto.setDegreeName(safeGet(info.getDegreeName(), i));
                    eduDto.setGraduationYn(safeGet(info.getGraduationYn(), i));
                    expertUserMapper.insertEducation(eduDto);
                }
            }
        }

        // 3. 경력 정보 저장
        if (info.getOrganization() != null && !info.getOrganization().isEmpty()) {
            for (int i = 0; i < info.getOrganization().size(); i++) {
                if (info.getOrganization().get(i) != null && !info.getOrganization().get(i).trim().isEmpty()) {
                    ExperienceDto expDto = new ExperienceDto();
                    expDto.setMemberId(memberId);
                    expDto.setOrganization(info.getOrganization().get(i));
                    expDto.setCareerMonth(safeGet(info.getCareerMonth(), i));
                    expDto.setCareerYear(safeGet(info.getCareerYear(), i));
                    expDto.setMedicalDepartments(safeGet(info.getMedicalDepartments(), i));
                    String isEmp = safeGet(info.getIsEmployed(), i);
                    expDto.setIsEmployed("true".equals(isEmp) ? "1" : "0");
                    expertUserMapper.insertExperience(expDto);
                }
            }
        }
        
        // 4. 정산 정보 저장
        if (info.getBankName() != null && !info.getBankName().isEmpty()) {
            ExpertSettlementDto setDto = new ExpertSettlementDto();
            setDto.setMemberId(memberId); 
            setDto.setBankName(info.getBankName());
            setDto.setAcctNo(info.getAcctNo());
            setDto.setTotAmt(BigDecimal.ZERO); 
            setDto.setPayYn("N"); 
            expertUserMapper.insertSettlement(setDto); 
        }
    }
    
    // 자격
    
    public void addCert(CertificateDto certDto, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String realPath = "D:/ofim_uploads/expert_docs/certs/";
                String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + ext;
                
                File dir = new File(realPath);
                if (!dir.exists()) dir.mkdirs();
                
                file.transferTo(new File(realPath, fileName));
                
                // DTO에 파일 경로 세팅 (DTO 변수명이 맞는지 확인하세요)
                certDto.setCertFilePath("/uploads/expert_docs/certs/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        expertUserMapper.insertCertificate(certDto);
    }
    // 자격증 삭제 처리
    @Transactional
    public void deleteCertificate(Long certId, Long memberId) {
        expertUserMapper.deleteCertificate(certId, memberId);
    }
	public void updateCert(CertificateDto certDto, MultipartFile file) {
		// 1. 자격증 파일을 저장할 물리적 경로 (프로필 사진과 폴더를 분리하는 것이 좋습니다)
	    String realPath = "D:/ofim_uploads/expert_docs/certs/";

	    // 2. 사용자가 새로운 파일을 업로드했는지 검사
	    if (file != null && !file.isEmpty()) {
	        try {
	            // 새 파일명 생성 (중복 방지 UUID)
	            String originalName = file.getOriginalFilename();
	            String ext = originalName.substring(originalName.lastIndexOf("."));
	            String fileName = UUID.randomUUID().toString() + ext;

	            // 폴더가 없으면 자동 생성
	            File dir = new File(realPath);
	            if (!dir.exists()) dir.mkdirs();
	            
	            // 지정된 경로에 파일 물리적 저장
	            File saveFile = new File(realPath, fileName);
	            file.transferTo(saveFile);

	            // 🌟 DTO에 브라우저 접근용 가상 경로 세팅 (변수명은 DTO에 맞게 수정하세요)
	            certDto.setCertFilePath("/uploads/expert_docs/certs/" + fileName);
	            
	            System.out.println("✅ 새로운 자격증 파일 업로드 완료: " + fileName);

	        } catch (IOException e) {
	            System.err.println("🚨 자격증 파일 저장 중 에러 발생!");
	            e.printStackTrace();
	        }
	    } else {
	        // 파일이 넘어오지 않았다면 경로를 셋팅하지 않습니다 (null 상태 유지).
	        System.out.println("ℹ️ 새로운 파일이 업로드되지 않아 기존 파일을 유지합니다.");
	    }

	    // 3. DB 업데이트 쿼리 실행
	    expertUserMapper.updateCert(certDto);
	}
    
    // 경력
	public void addExp(ExperienceDto expDto) {
        expertUserMapper.insertExperience(expDto); 
    }
    public void updateExp(ExperienceDto expDto) {
        expertUserMapper.updateExp(expDto);
    }
    public void deleteExp(Long careerId) {
    	expertUserMapper.deleteExp(careerId);
    }

    // 학력
    public void addEdu(EducationDto eduDto) {
        expertUserMapper.insertEducation(eduDto); 
    }
    public void updateEdu(EducationDto eduDto) {
    	expertUserMapper.updateEdu(eduDto);
    }
    public void deleteEdu(Long careerId) {
    	expertUserMapper.deleteEdu(careerId);
    }
    
    // 정산
    public void updateSettlement(ExpertSettlementDto dto) {
        expertUserMapper.updateSettlement(dto);
    }

    // ==========================================
    // 🌟 [여기에 추가됨] 화면에 뿌려줄 데이터 조회(SELECT) 로직
    // ==========================================

    public MemberDto getMemberById(Long memberId) {
        return expertUserMapper.selectMemberById(memberId);
    }

    public ExpertUserDto getExpertById(Long memberId) {
        return expertUserMapper.selectExpertById(memberId);
    }

    public List<CertificateDto> getCertList(Long memberId) {
        return expertUserMapper.selectCertList(memberId);
    }

    public List<EducationDto> getEduList(Long memberId) {
        return expertUserMapper.selectEduList(memberId);
    }

    public List<ExperienceDto> getExpList(Long memberId) {
        return expertUserMapper.selectExpList(memberId);
    }

    public ExpertSettlementDto getSettlement(Long memberId) {
        return expertUserMapper.selectSettlement(memberId);
    }
    
    public void updateOnlineStatus(Long memberId, boolean isOnline) {
    	expertUserMapper.updateOnlineStatus(memberId, isOnline);
	
   } 


	

	
}