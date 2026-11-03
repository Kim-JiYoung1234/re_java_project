package com.human.ofim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.CertificateDto;
import com.human.ofim.dto.ConsultReserveDto;
import com.human.ofim.dto.EducationDto;
import com.human.ofim.dto.ExperienceDto;
import com.human.ofim.dto.ExpertInfoDto;
import com.human.ofim.dto.ExpertSettlementDto;
import com.human.ofim.dto.ExpertUserDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.dto.ReviewDto;
import com.human.ofim.dto.InquiryDto;
import com.human.ofim.dto.HealthInfoDto;
import com.human.ofim.dto.BoardDto;
import com.human.ofim.dto.BoardReplyDto;
import com.human.ofim.service.AuthService;
import com.human.ofim.service.ExpertService;
import com.human.ofim.service.FitnessService;
import com.human.ofim.service.HealthService;
import com.human.ofim.service.MemberService; 
import com.human.ofim.service.BoardService;
import com.human.ofim.service.BookmarkService;
import com.human.ofim.service.ConsultService;
import com.human.ofim.mapper.ConsultMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MypageController {

	private final MemberService memberService;
	private final AuthService authService;
	private final ExpertService expertService;
	private final HealthService healthService;
	private final BoardService boardService;
	private final ConsultService consultService;
	private final FitnessService fitnessService;
    private final ConsultMapper consultMapper;
    private final BookmarkService bookmarkService;

	// =========================================================================
	// [GET] 마이페이지 메인 화면 (건강 + 전문가 + 내 게시글 통합)
	// =========================================================================
    @GetMapping("/mypage/main")
    public String showMain(HttpSession session, Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        Long memberId = loginUser.getMemberId();
        int amount = 8;
        int pageBlock = 5;

        // ==========================================
        // 1. 공통 데이터 조회 (프로필, 건강계획)
        // ==========================================
        MemberDto dbMember = expertService.getMemberById(memberId);
        HealthInfoDto healthInfo = healthService.getMyPlan(memberId);

        // -----------------------------------------------------------
        // 2. [내가 작성한 게시글] 페이징 조회 
        // -----------------------------------------------------------
        List<BoardDto> myPosts = boardService.findAll(null, "전체", keyword, page, "latest", memberId.intValue());
        int totalCount = boardService.getTotalCount(null, "전체", keyword, memberId.intValue());

        int totalPages = (int) Math.ceil((double) totalCount / amount);
        if (totalPages == 0) totalPages = 1;

        int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages);

        // -----------------------------------------------------------
        // 3. [내가 작성한 댓글] 페이징 조회 
        // -----------------------------------------------------------
        List<BoardReplyDto> myReplies = boardService.getMyRepliesPaging(memberId.intValue(), "", 1, amount);
        int replyTotalCount = boardService.countMyReplies(memberId.intValue(), "");

        int replyTotalPages = (int) Math.ceil((double) replyTotalCount / amount);
        if (replyTotalPages == 0) replyTotalPages = 1;

        int replyStartPage = 1; 
        int replyEndPage = Math.min(replyStartPage + pageBlock - 1, replyTotalPages);

        // -----------------------------------------------------------
        // 4. [내가 좋아요 한 글] 페이징 조회
        // -----------------------------------------------------------
        List<BoardDto> myLikedPosts = boardService.getMyLikedPostsPaging(memberId.intValue(), "", 1, amount);
        int likedTotalCount = boardService.countMyLikedPostsPaging(memberId.intValue(), "");

        int likedTotalPages = (int) Math.ceil((double) likedTotalCount / amount);
        if (likedTotalPages == 0) likedTotalPages = 1;

        int likedStartPage = 1; 
        int likedEndPage = Math.min(likedStartPage + pageBlock - 1, likedTotalPages);

        // -----------------------------------------------------------
        // 5. [내가 쓴 리뷰] 페이징 조회
        // -----------------------------------------------------------
        List<ReviewDto> myReviews = fitnessService.getMyReviewsPaging(memberId, "", 1, amount);
        int reviewTotalCount = fitnessService.countMyReviewsPaging(memberId, "");

        int reviewTotalPages = (int) Math.ceil((double) reviewTotalCount / amount);
        if (reviewTotalPages == 0) reviewTotalPages = 1;

        int reviewStartPage = 1; 
        int reviewEndPage = Math.min(reviewStartPage + pageBlock - 1, reviewTotalPages);
        
        // -----------------------------------------------------------
        // 6. [내가 즐겨찾기(스크랩) 한 글] 페이징 조회
        // -----------------------------------------------------------
        List<BoardDto> myBookmarks = bookmarkService.getMyBookmarksPaging(memberId.intValue(), "", 1, amount);
        int bookmarkTotalCount = bookmarkService.countMyBookmarks(memberId.intValue(), "");

        int bookmarkTotalPages = (int) Math.ceil((double) bookmarkTotalCount / amount);
        if (bookmarkTotalPages == 0) bookmarkTotalPages = 1;

        int bookmarkStartPage = 1; 
        int bookmarkEndPage = Math.min(bookmarkStartPage + pageBlock - 1, bookmarkTotalPages);

        // ==========================================
        // 6. 공통 Model 데이터 세팅
        // ==========================================
        model.addAttribute("userType", loginUser.getMemberType());
        model.addAttribute("user", dbMember);
        model.addAttribute("member", dbMember);
        model.addAttribute("health", healthInfo);

        // [1] 게시글 탭 데이터
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        // [2] 댓글 탭 데이터
        model.addAttribute("myReplies", myReplies);
        model.addAttribute("replyCurrentPage", 1);
        model.addAttribute("replyTotalPages", replyTotalPages);
        model.addAttribute("replyStartPage", replyStartPage);
        model.addAttribute("replyEndPage", replyEndPage);
        model.addAttribute("replyKeyword", ""); 

        // [3] 좋아요 탭 데이터
        model.addAttribute("myLikedPosts", myLikedPosts);
        model.addAttribute("likedCurrentPage", 1);
        model.addAttribute("likedTotalPages", likedTotalPages);
        model.addAttribute("likedStartPage", likedStartPage);
        model.addAttribute("likedEndPage", likedEndPage);
        model.addAttribute("likedKeyword", "");

        // [4] 리뷰 탭 데이터
        model.addAttribute("myReviews", myReviews);
        model.addAttribute("reviewCurrentPage", 1);
        model.addAttribute("reviewTotalPages", reviewTotalPages);
        model.addAttribute("reviewStartPage", reviewStartPage);
        model.addAttribute("reviewEndPage", reviewEndPage);
        model.addAttribute("reviewKeyword", "");
        
        // [5] 즐겨찾기 탭 데이터 추가
        model.addAttribute("myBookmarks", myBookmarks);
        model.addAttribute("bookmarkCurrentPage", 1);
        model.addAttribute("bookmarkTotalPages", bookmarkTotalPages);
        model.addAttribute("bookmarkStartPage", bookmarkStartPage);
        model.addAttribute("bookmarkEndPage", bookmarkEndPage);
        model.addAttribute("bookmarkKeyword", "");

        // ==========================================
        // 🌟 7. [일반 회원] 전용 데이터 세팅 (상담/예약 및 1:1 문의)
        // ==========================================
        if ("NORMAL".equals(loginUser.getMemberType()) || "일반회원".equals(loginUser.getMemberType())) {
            // 1. 내가 신청한 모든 상담 내역 가져오기
            List<Map<String, Object>> allConsultList = consultService.getConsultList(memberId); 
            
            // 🌟 순수 '상담 예약'만 담을 빈 리스트 준비
            List<Map<String, Object>> myConsultBookingList = new ArrayList<>();

            if (allConsultList != null) {
                for (Map<String, Object> consult : allConsultList) {
                    Long consultId = Long.valueOf(String.valueOf(consult.get("consult_id")));
                    List<InquiryDto> inquiryList = consultMapper.getInquiryList(consultId);
                    consult.put("inquiryList", inquiryList);
                    
                    String category = (String) consult.get("category");
                    if (category == null || (!category.contains("문의") && !category.contains("1:1"))) {
                        myConsultBookingList.add(consult);
                    }
                }
            }
            
            // [예약 내역 탭] ➡️ 1:1 문의가 쏙 빠진 순수 예약 리스트 전달
            model.addAttribute("myConsultList", myConsultBookingList);             
            // [1:1 문의 탭] ➡️ 전체 리스트 전달 (HTML에서 알아서 '문의'만 필터링해서 보여줌)
            model.addAttribute("consultList", allConsultList);
        }
        
        // ==========================================
        // 8. [전문가 회원] 전용 데이터 세팅
        // ==========================================
        if ("EXPERT".equals(loginUser.getMemberType()) || "전문가회원".equals(loginUser.getMemberType())) {
            model.addAttribute("expert", expertService.getExpertById(memberId));
            model.addAttribute("certList", expertService.getCertList(memberId));
            model.addAttribute("eduList", expertService.getEduList(memberId));
            model.addAttribute("expList", expertService.getExpList(memberId));
            model.addAttribute("settlement", expertService.getSettlement(memberId));
            
            // 상담 내역 전체 목록 가져오기
            List<Map<String, Object>> consultList = consultService.getConsultsForExpert(memberId);
            
            // 각 상담글마다 대화 내역(inquiryList)을 조회해서 맵에 꽂아주기
            if (consultList != null) {
                for (Map<String, Object> consult : consultList) {
                    Long consultId = Long.valueOf(String.valueOf(consult.get("consult_id")));
                    List<InquiryDto> inquiryList = consultMapper.getInquiryList(consultId); 
                    consult.put("inquiryList", inquiryList); 
                }
            }

            model.addAttribute("consultList", consultList); 
        }

        return "mypage/main";
    }

    // =========================================================================
    // 1:1 문의 답변 등록 API 추가 (전문가용)
    // =========================================================================
    @PostMapping("/expert/inquiry/answer")
    @ResponseBody
    public String submitInquiryAnswer(@RequestParam("consult_id") Long consultId, 
                                      @RequestParam("answer") String answer) {
        
        // 서비스에게 일을 시키고 결과를 받습니다. (아까 만들어둔 서비스)
        boolean isSuccess = consultService.submitExpertAnswer(consultId, answer);
        
        if (isSuccess) {
            return "success";
        } else {
            return "fail";
        }
    }

	// =========================================================================
	// 이하 회원정보/전문가정보/Ajax 페이징 수정 관련 컨트롤러 로직 (기존과 동일하게 유지)
	// =========================================================================

	@PostMapping("/user/edit-single")
	public String editSingleInfo(@RequestParam("fieldName") String fieldName,
			@RequestParam("fieldValue") String fieldValue, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/auth/login";

		// DB 업데이트
		memberService.updateMemberField(loginUser.getMemberId(), fieldName, fieldValue);

		// 세션 정보 즉시 갱신
		if ("nickname".equals(fieldName))
			loginUser.setNickname(fieldValue);
		else if ("phone".equals(fieldName))
			loginUser.setPhone(fieldValue);
		else if ("email".equals(fieldName))
			loginUser.setEmail(fieldValue);

		return "redirect:/mypage/main";
	}

	@PostMapping("/user/edit-address")
	public String editAddress(MemberDto addressDto, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/auth/login";

		addressDto.setMemberId(loginUser.getMemberId());
		memberService.updateMemberAddress(addressDto);

		// 세션 갱신
		loginUser.setZipcode(addressDto.getZipcode());
		loginUser.setAddress(addressDto.getAddress());
		loginUser.setAddress2(addressDto.getAddress2());

		return "redirect:/mypage/main";
	}

	@PostMapping("/expert/edit-profile")
	public String editExpertProfile(ExpertUserDto expertDto,
			@RequestParam(value = "profileImageFile", required = false) MultipartFile file, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/auth/login";

		expertDto.setMemberId(loginUser.getMemberId());
		expertService.updateExpertProfile(expertDto, file);

		return "redirect:/mypage/main";
	}
	
	@PostMapping("/expert/edit-cert")
    public String editExpertCert(CertificateDto certDto, 
                                 @RequestParam(value="certFile", required=false) MultipartFile file) {
        expertService.updateCert(certDto, file);
        return "redirect:/mypage/main";
    }
	
	@PostMapping("/expert/edit-edu")
    public String editExpertEdu(EducationDto eduDto) {
        expertService.updateEdu(eduDto);
        return "redirect:/mypage/main"; 
    }
	
	@PostMapping("/expert/edit-exp")
    public String editExpertExp(ExperienceDto expDto) {
        expertService.updateExp(expDto);
        return "redirect:/mypage/main";
    }
	
	@PostMapping("/expert/edit-settlement")
	public String editSettlement(ExpertSettlementDto settledto, HttpSession session) {
	    MemberDto loginUser = (MemberDto) session.getAttribute("user");
	    if (loginUser == null) {
	        return "redirect:/auth/login";
	    }
	    settledto.setMemberId(loginUser.getMemberId());
	    expertService.updateSettlement(settledto);
	    
	    return "redirect:/mypage/main"; 
	}

	@PostMapping("/expert/add-cert")
	public String addExpertCert(ExpertInfoDto info, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/auth/login";
		expertService.saveExpertAdditionalInfo(info, loginUser.getMemberId());
		return "redirect:/mypage/main";
	}
	
	@PostMapping("/expert/add-edu")
    public String addExpertEdu(EducationDto eduDto, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login"; 
        }
        eduDto.setMemberId(loginUser.getMemberId());
        expertService.addEdu(eduDto);
        return "redirect:/mypage/main";
    }
	
	@PostMapping("/expert/add-exp")
    public String addExpertExp(ExperienceDto expDto, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login";
        }
        expDto.setMemberId(loginUser.getMemberId());
        expertService.addExp(expDto);
        return "redirect:/mypage/main";
    }

	@PostMapping("/expert/delete-cert")
	public String deleteExpertCert(@RequestParam("certificationId") Long certId, HttpSession session) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null)
			return "redirect:/auth/login";
		expertService.deleteCertificate(certId, loginUser.getMemberId());
		return "redirect:/mypage/main";
	}
	
	@PostMapping("/expert/delete-edu")
    public String deleteExpertEdu(@RequestParam("memberId") Long memberId) {
        expertService.deleteEdu(memberId);
        return "redirect:/mypage/main";
    }
	
	@PostMapping("/expert/delete-exp")
    public String deleteExpertExp(@RequestParam("memberId") Long memberId) {
        expertService.deleteExp(memberId);
        return "redirect:/mypage/main";
    }

	@PostMapping("/mypage/expertDetails")
	public String expertAddInfo(ExpertInfoDto info) {
		if ("EXPERT".equals(info.getMemberType())) {
			MemberDto member = convertToMemberDto(info);
			ExpertUserDto expert = convertToExpertDto(info);
			CertificateDto cert = convertToCertDto(info);
			authService.registerExpertMember(member, expert, cert);
			expertService.saveExpertAdditionalInfo(info, member.getMemberId());
		} else {
			authService.register(convertToMemberDto(info));
		}
		return "redirect:/auth/home?registerSuccess=true";
	}
	
	@GetMapping("/mypage/posts/fragment")
	public String getMyPostsFragment(HttpSession session, Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null) return "redirect:/auth/login";

		Long memberId = loginUser.getMemberId();

		List<BoardDto> myPosts = boardService.findAll(null, "전체", keyword, page, "latest", memberId.intValue());
		int totalCount = boardService.getTotalCount(null, "전체", keyword, memberId.intValue());

		int amount = 8; 
		int totalPages = (int) Math.ceil((double) totalCount / amount);
		if (totalPages == 0) totalPages = 1;

		int pageBlock = 5;
		int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
		int endPage = Math.min(startPage + pageBlock - 1, totalPages);

		model.addAttribute("myPosts", myPosts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("keyword", keyword);

		return "mypage/user_info :: my-posts-fragment"; 
	}
	
	@GetMapping("/mypage/replies/fragment")
	public String getMyRepliesFragment(HttpSession session, Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null) return "redirect:/auth/login";

		Long memberId = loginUser.getMemberId();
		int amount = 10; 

		List<BoardReplyDto> myReplies = boardService.getMyRepliesPaging(memberId.intValue(), keyword, page, amount);
		int totalCount = boardService.countMyReplies(memberId.intValue(), keyword);

		int totalPages = (int) Math.ceil((double) totalCount / amount);
		if (totalPages == 0) totalPages = 1;

		int pageBlock = 5;
		int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
		int endPage = Math.min(startPage + pageBlock - 1, totalPages);

		model.addAttribute("myReplies", myReplies);
		model.addAttribute("replyCurrentPage", page);
		model.addAttribute("replyTotalPages", totalPages);
		model.addAttribute("replyStartPage", startPage);
		model.addAttribute("replyEndPage", endPage);
		model.addAttribute("replyKeyword", keyword);

		return "mypage/user_info :: my-replies-fragment"; 
	}
	
	@GetMapping("/mypage/likes/fragment")
	public String getMyLikedPostsFragment(HttpSession session, Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null) return "redirect:/auth/login";

		Long memberId = loginUser.getMemberId();
		int amount = 8; 

		List<BoardDto> myLikedPosts = boardService.getMyLikedPostsPaging(memberId.intValue(), keyword, page, amount);
		int totalCount = boardService.countMyLikedPostsPaging(memberId.intValue(), keyword);

		int totalPages = (int) Math.ceil((double) totalCount / amount);
		if (totalPages == 0) totalPages = 1;

		int pageBlock = 5;
		int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
		int endPage = Math.min(startPage + pageBlock - 1, totalPages);

		model.addAttribute("myLikedPosts", myLikedPosts);
		model.addAttribute("likedCurrentPage", page);
		model.addAttribute("likedTotalPages", totalPages);
		model.addAttribute("likedStartPage", startPage);
		model.addAttribute("likedEndPage", endPage);
		model.addAttribute("likedKeyword", keyword);

		return "mypage/user_info :: my-likes-fragment"; 
	}
	
	@GetMapping("/mypage/reviews/fragment")
	public String getMyReviewsFragment(HttpSession session, Model model,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null) return "redirect:/auth/login";

		Long memberId = loginUser.getMemberId();
		int amount = 8; 

		List<ReviewDto> myReviews = fitnessService.getMyReviewsPaging(memberId, keyword, page, amount);
		int totalCount = fitnessService.countMyReviewsPaging(memberId, keyword);

		int totalPages = (int) Math.ceil((double) totalCount / amount);
		if (totalPages == 0) totalPages = 1;

		int pageBlock = 5;
		int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
		int endPage = Math.min(startPage + pageBlock - 1, totalPages);

		model.addAttribute("myReviews", myReviews);
		model.addAttribute("reviewCurrentPage", page);
		model.addAttribute("reviewTotalPages", totalPages);
		model.addAttribute("reviewStartPage", startPage);
		model.addAttribute("reviewEndPage", endPage);
		model.addAttribute("reviewKeyword", keyword);

		return "mypage/user_info :: my-reviews-fragment"; 
	}
	
	@GetMapping("/mypage/bookmarks/fragment")
    public String getBookmarksFragment(HttpSession session, Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) return "redirect:/auth/login";

        int memberId = loginUser.getMemberId().intValue();
        int amount = 8;
        int pageBlock = 5;

        // 1. 방금 삭제된 상태가 반영된 최신 즐겨찾기 리스트를 DB에서 다시 가져옵니다.
        List<BoardDto> myBookmarks = bookmarkService.getMyBookmarksPaging(memberId, keyword, page, amount);
        int bookmarkTotalCount = bookmarkService.countMyBookmarks(memberId, keyword);

        // 2. 페이징 계산
        int bookmarkTotalPages = (int) Math.ceil((double) bookmarkTotalCount / amount);
        if (bookmarkTotalPages == 0) bookmarkTotalPages = 1;

        int bookmarkStartPage = ((page - 1) / pageBlock) * pageBlock + 1;
        int bookmarkEndPage = Math.min(bookmarkStartPage + pageBlock - 1, bookmarkTotalPages);

        // 3. 모델에 새 데이터 담기
        model.addAttribute("myBookmarks", myBookmarks);
        model.addAttribute("bookmarkCurrentPage", page);
        model.addAttribute("bookmarkTotalPages", bookmarkTotalPages);
        model.addAttribute("bookmarkStartPage", bookmarkStartPage);
        model.addAttribute("bookmarkEndPage", bookmarkEndPage);
        model.addAttribute("bookmarkKeyword", keyword);

        return "mypage/user_info :: my-bookmarks-fragment";
    }
	
	@GetMapping("/mypage/centers/fragment")
	public String getMyCentersFragment(HttpSession session, Model model,
	                                   @RequestParam(value = "page", defaultValue = "1") int page,
	                                   @RequestParam(value = "keyword", defaultValue = "") String keyword) {
	    
	    MemberDto loginUser = (MemberDto) session.getAttribute("user");
	    if (loginUser == null) return "redirect:/auth/login";

	    Long memberId = loginUser.getMemberId();
	    int amount = 6; // 한 페이지에 카드 6개씩!

	    List<Map<String, Object>> myCenters = fitnessService.getMyBookmarkedCenters(memberId, keyword, page, amount);
	    int totalCount = fitnessService.countMyBookmarkedCenters(memberId, keyword);
	    
	    int totalPages = (int) Math.ceil((double) totalCount / amount);
	    if (totalPages == 0) totalPages = 1;
	    
	    int pageBlock = 5;
	    int startPage = ((page - 1) / pageBlock) * pageBlock + 1;
	    int endPage = Math.min(startPage + pageBlock - 1, totalPages);

	    model.addAttribute("myCenters", myCenters);
	    model.addAttribute("centerCurrentPage", page);
	    model.addAttribute("centerTotalPages", totalPages);
	    model.addAttribute("centerStartPage", startPage);
	    model.addAttribute("centerEndPage", endPage);
	    model.addAttribute("centerKeyword", keyword);

	    return "mypage/user_info :: center-pick-fragment"; 
	}
	
	@PostMapping("/mypage/reviews/delete")
	@ResponseBody
	public String deleteReview(HttpSession session, @RequestParam("reviewId") int reviewId) {
		MemberDto loginUser = (MemberDto) session.getAttribute("user");
		if (loginUser == null) return "fail";

		boolean isDeleted = fitnessService.deleteReview(reviewId, loginUser.getMemberId());
		return isDeleted ? "success" : "fail";
	}
	
	@PostMapping("/mypage/activity/delete")
	@ResponseBody 
	public String deleteActivity(HttpSession session, 
	                             @RequestParam("id") int id, 
	                             @RequestParam("type") String type) {
	                                 
	    MemberDto loginUser = (MemberDto) session.getAttribute("user");
	    if (loginUser == null) {
	        return "fail"; 
	    }
	    
	    // 🌟 두 가지 타입을 모두 준비!
	    Long memberIdLong = loginUser.getMemberId();     // Long 타입
	    int memberIdInt = memberIdLong.intValue();       // int 타입
	    
	    boolean isSuccess = false;

	    try {
	        if ("post".equals(type)) {
	            isSuccess = boardService.deleteMyPost(id, memberIdInt);
	        } else if ("reply".equals(type)) {
	            isSuccess = boardService.deleteMyReply(id, memberIdInt);
	        } else if ("like".equals(type)) {
	            isSuccess = boardService.deleteMyLike(id, memberIdInt);
	        } 
	        else if ("bookmark".equals(type)) {
	            // 🌟 BookmarkService는 (Long 회원번호, int 글번호) 순서입니다.
	            bookmarkService.toggleBookmark(memberIdLong, id);
	            isSuccess = true;
	        } 
	        else if ("center".equals(type)) {
	            // 🌟 FitnessService는 (Integer 센터번호, Long 회원번호) 순서입니다.
	            fitnessService.toggleBookmark(id, memberIdLong);
	            isSuccess = true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "fail";
	    }

	    return isSuccess ? "success" : "fail";
	}
    
    
	private MemberDto convertToMemberDto(ExpertInfoDto info) {
		MemberDto dto = new MemberDto();
		dto.setLoginId(info.getLoginId());
		dto.setPassword(info.getPassword());
		dto.setUserName(info.getUserName());
		dto.setEmail(info.getEmail());
		dto.setNickname(info.getNickname());
		dto.setMemberType(info.getMemberType());
		dto.setPhone(info.getPhone());
		dto.setAddress(info.getAddress());
		dto.setGender(info.getGender());
		return dto;
	}

	private ExpertUserDto convertToExpertDto(ExpertInfoDto info) {
		ExpertUserDto dto = new ExpertUserDto();
		dto.setSpecialty(info.getSpecialty());
		dto.setExpertIntroduce(info.getExpertIntroduce());
		return dto;
	}

	private CertificateDto convertToCertDto(ExpertInfoDto info) {
		CertificateDto dto = new CertificateDto();
		if (info.getCertName() != null && !info.getCertName().isEmpty()) {
			dto.setCertName(info.getCertName().get(0));
			dto.setIssuer(info.getIssuer().get(0));
			dto.setCertNo(info.getCertNo().get(0));
			dto.setIssuedDate(info.getIssuedDate().get(0));
			dto.setExpiredDate(info.getExpiredDate().get(0));
		}
		return dto;
	}
	
	// [추가] 파트너 리뷰 관리 Ajax 전용 메서드
	// [수정 후] 🌟 조각이 실제로 작성된 파일명(partner_info)을 적어줘야 합니다.
    @GetMapping("/mypage/partner/reviews/fragment")
    public String getPartnerReviewsFragment(HttpSession session, Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) return "redirect:/auth/login";

        Long partnerId = loginUser.getMemberId();
        int amount = 5; 

        List<ReviewDto> partnerReviews = fitnessService.getPartnerReviewsPaging(partnerId, page, amount);
        
        // 💡 데이터가 오는지 확인하기 위한 로그 (이클립스 콘솔에서 확인 가능)
        System.out.println(">>> 파트너 ID: " + partnerId);
        System.out.println(">>> 조회된 리뷰 개수: " + (partnerReviews != null ? partnerReviews.size() : 0));

        int totalCount = fitnessService.countPartnerReviews(partnerId);
        int totalPages = (int) Math.ceil((double) totalCount / amount);
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("partnerReviews", partnerReviews);
        model.addAttribute("prCurrentPage", page);
        model.addAttribute("prTotalPages", totalPages);

        // 🌟 여기가 핵심입니다! main이 아니라 partner_info에서 조각을 찾아야 합니다.
        return "mypage/partner_info :: partner-reviews-fragment";
    }

    @PostMapping("/mypage/partner/reviews/reply")
    @ResponseBody
    public String addReviewReply(HttpSession session, @RequestParam("reviewId") int reviewId, @RequestParam("replyContent") String replyContent) {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) return "fail";
        boolean isSuccess = fitnessService.updateReviewReply(reviewId, replyContent);
        return isSuccess ? "success" : "fail";
    }
}