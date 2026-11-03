package com.human.ofim.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.ofim.dto.AdminExpertClientDto;
import com.human.ofim.dto.AdminExpertReviewSummaryDto;
import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ConsultDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.service.AdminExpertService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/expert")
@RequiredArgsConstructor
public class AdminExpertController {

    private static final String UPLOAD_ROOT = "D:/ofim_uploads";

    private final AdminExpertService adminExpertService;

    /**
     * 전문가 운영 목록
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "status", required = false) Integer status,
                       @RequestParam(value = "minConsult", required = false) Integer minConsult,
                       @RequestParam(value = "sort", required = false, defaultValue = "CREATED_DESC") String sort,
                       Model model) {

        model.addAttribute("list", adminExpertService.getExpertList(keyword, status, minConsult, sort));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("minConsult", minConsult);
        model.addAttribute("sort", sort);

        model.addAttribute("pageTitle", "전문가 관리");
        model.addAttribute("pageDesc", "전문가 운영 및 상태 관리");
        model.addAttribute("activeMenu", "expert");
        model.addAttribute("reviewMode", false);

        return "admin/expert/experts";
    }

    /**
     * 전문가 심사 목록
     */
    @GetMapping("/review")
    public String review(@RequestParam(value = "approveStatus", required = false) Integer approveStatus,
                         @RequestParam(value = "memberStatus", required = false) String memberStatus,
                         @RequestParam(value = "onlineYn", required = false) String onlineYn,
                         @RequestParam(value = "searchType", required = false, defaultValue = "all") String searchType,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "1") int page,
                         Model model) {

        if (page < 1) {
            page = 1;
        }

        int size = 10;
        int offset = (page - 1) * size;

        List<ExpertUserDto> expertList = adminExpertService.getExpertReviewList(
                approveStatus, memberStatus, onlineYn, searchType, keyword, offset, size
        );

        int totalCount = adminExpertService.getExpertReviewCount(
                approveStatus, memberStatus, onlineYn, searchType, keyword
        );

        int totalPages = (int) Math.ceil((double) totalCount / size);

        AdminExpertReviewSummaryDto summary = adminExpertService.getExpertReviewSummary();

        model.addAttribute("expertList", expertList);
        model.addAttribute("summary", summary);

        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("approveStatus", approveStatus);
        model.addAttribute("memberStatus", memberStatus);
        model.addAttribute("onlineYn", onlineYn);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        model.addAttribute("pageTitle", "전문가 승인관리");
        model.addAttribute("pageDesc", "전문가 신청 검토 및 승인 관리");
        model.addAttribute("activeMenu", "expert_review");

        return "admin/expert/expert-review";
    }

    /**
     * 전문가 운영 상세
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("memberId") Long memberId, Model model) {

        ExpertUserDto expert = adminExpertService.getExpertDetail(memberId);
        List<AdminExpertClientDto> clientList = adminExpertService.getExpertClientList(memberId);
        List<ConsultDto> consultList = adminExpertService.getExpertConsultList(memberId);
        List<CertificateDto> certList = adminExpertService.getCertList(memberId);

        model.addAttribute("expert", expert);
        model.addAttribute("certList", certList);
        model.addAttribute("consultStats", adminExpertService.getExpertConsultStats(memberId));
        model.addAttribute("clientList", clientList);
        model.addAttribute("consultList", consultList);
        model.addAttribute("joinInfo", adminExpertService.getExpertJoinInfo(memberId));

        model.addAttribute("clientPreviewList",
                clientList == null ? List.of() : clientList.stream().limit(5).toList());

        model.addAttribute("consultPreviewList",
                consultList == null ? List.of() : consultList.stream().limit(5).toList());

        model.addAttribute("certPreviewList",
                certList == null ? List.of() : certList.stream().limit(5).toList());

        model.addAttribute("pageTitle", "전문가 상세");
        model.addAttribute("pageDesc", "전문가 정보 / 서류 / 상담 현황");
        model.addAttribute("activeMenu", "expert_detail");

        return "admin/expert/detail";
    }

    /**
     * 전문가 심사 상세
     */
    @GetMapping("/review/{memberId}")
    public String reviewDetail(@PathVariable("memberId") Long memberId, Model model) {

        model.addAttribute("expert", adminExpertService.getExpertReviewDetail(memberId));
        model.addAttribute("certList", adminExpertService.getCertList(memberId));

        model.addAttribute("careerList", adminExpertService.getExpertCareer(memberId));
        model.addAttribute("educationList", adminExpertService.getExpertEducation(memberId));
        model.addAttribute("settlement", adminExpertService.getExpertSettlement(memberId));
        model.addAttribute("totalCareerText", adminExpertService.getTotalCareerText(memberId));

        model.addAttribute("pageTitle", "전문가 심사 상세");
        model.addAttribute("allCertApproved",
        	    adminExpertService.isAllCertApproved(memberId));
        model.addAttribute("activeMenu", "expert_review_detail");

        return "admin/expert/review-detail";
    }

    /**
     * 회원 상태 변경 (운영 상세용)
     */
    @PostMapping("/member-status")
    public String updateMemberStatus(@RequestParam("memberId") Long memberId,
                                     @RequestParam("actionType") String actionType,
                                     @RequestParam(value = "reason", required = false) String reason,
                                     RedirectAttributes ra) {

        adminExpertService.updateExpertMemberStatus(memberId, actionType, reason);
        ra.addFlashAttribute("successMessage", "전문가 상태가 변경되었습니다.");
        return "redirect:/admin/expert/detail?memberId=" + memberId;
    }

    /**
     * 최종 승인 (심사 상세용)
     */
    @PostMapping("/approve")
    public String approve(@RequestParam("memberId") Long memberId,
                          RedirectAttributes ra) {

        if (!adminExpertService.isAllCertApproved(memberId)) {
            ra.addFlashAttribute("errorMessage", "모든 서류 승인 후 승인 가능합니다.");
            return "redirect:/admin/expert/review/" + memberId;
        }

        adminExpertService.updateExpertApproveStatus(memberId, 1);

        ra.addFlashAttribute("successMessage", "전문가 승인 처리되었습니다.");
        return "redirect:/admin/expert/review/" + memberId;
    }

    /**
     * 최종 반려 (심사 상세용)
     */
    @PostMapping("/reject")
    public String reject(@RequestParam("memberId") Long memberId,
                         @RequestParam(value = "reason", required = false) String reason,
                         RedirectAttributes ra) {

        adminExpertService.updateExpertApproveStatus(memberId, 2);

        if (reason != null && !reason.trim().isEmpty()) {
            adminExpertService.updateExpertRejectReason(memberId, reason.trim());
        }

        ra.addFlashAttribute("successMessage", "전문가 반려 처리되었습니다.");
        return "redirect:/admin/expert/review/" + memberId;
    }

    /**
     * 서류 승인 (심사 상세용)
     */
    @PostMapping("/cert/approve")
    public String approveCert(@RequestParam("certId") Long certId,
                              @RequestParam("memberId") Long memberId,
                              RedirectAttributes ra) {

        adminExpertService.updateCertVerifyStatus(certId, 1, null);
        ra.addFlashAttribute("successMessage", "서류 승인 처리되었습니다.");
        return "redirect:/admin/expert/review/" + memberId;
    }

    /**
     * 서류 반려 (심사 상세용)
     */
    @PostMapping("/cert/reject")
    public String rejectCert(@RequestParam("certId") Long certId,
                             @RequestParam("memberId") Long memberId,
                             @RequestParam(value = "reason", required = false) String reason,
                             RedirectAttributes ra) {

        adminExpertService.updateCertVerifyStatus(certId, 2, reason);
        ra.addFlashAttribute("successMessage", "서류 반려 처리되었습니다.");
        return "redirect:/admin/expert/review/" + memberId;
    }

    /**
     * 자격증 다운로드
     */
    @GetMapping("/cert/download")
    public ResponseEntity<Resource> downloadCert(@RequestParam("certId") Long certId) throws Exception {

        CertificateDto cert = adminExpertService.getCertById(certId);
        String webPath = cert.getCertFilePath();

        if (webPath == null || webPath.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String normalized = webPath.startsWith("/") ? webPath.substring(1) : webPath;

        Path rootPath = Paths.get(UPLOAD_ROOT).toAbsolutePath().normalize();
        Path filePath = rootPath.resolve(normalized).normalize();

        if (!filePath.startsWith(rootPath)) {
            return ResponseEntity.badRequest().build();
        }

        UrlResource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String filename = resource.getFilename();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }

    /**
     * 자격증 미리보기
     */
    @GetMapping("/cert/preview")
    public ResponseEntity<Resource> previewCert(@RequestParam("certId") Long certId) throws Exception {

        CertificateDto cert = adminExpertService.getCertById(certId);
        String webPath = cert.getCertFilePath();

        if (webPath == null || webPath.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String normalized = webPath.startsWith("/") ? webPath.substring(1) : webPath;

        Path rootPath = Paths.get(UPLOAD_ROOT).toAbsolutePath().normalize();
        Path filePath = rootPath.resolve(normalized).normalize();

        if (!filePath.startsWith(rootPath)) {
            return ResponseEntity.badRequest().build();
        }

        UrlResource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(resource);
    }
    
    @GetMapping("/settlement")
    public String settlement(@RequestParam(value = "month", required = false) String month,
                             Model model) {

        if (month == null || month.isBlank()) {
            month = java.time.LocalDate.now().toString().substring(0, 7); // yyyy-MM
        }

        model.addAttribute("list", adminExpertService.getSettlementList(month));

        model.addAttribute("month", month);
        model.addAttribute("activeMenu", "expert_settlement");
        model.addAttribute("pageTitle", "전문가 정산관리");

        return "admin/expert/expert-settlement";
    }
    
    @GetMapping("/settlement/detail")
    @ResponseBody
    public List<ExpertSettlementDto> settlementDetail(@RequestParam("expertId") Long expertId,
                                                      @RequestParam("month") String month) {
        return adminExpertService.getSettlementDetail(expertId, month);
    }
}