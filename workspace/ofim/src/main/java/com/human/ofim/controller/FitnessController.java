package com.human.ofim.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.CenterTicketDto;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.MemberDto;
import com.human.ofim.dto.ReviewDto;
import com.human.ofim.service.FitnessService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class FitnessController {

    @Autowired
    private FitnessService fitnessService;

    @GetMapping("/fitness/register")
    public String registerForm() {
        return "fitness/fitness_register";
    }

    @PostMapping("/fitness/register")
    public void registerFitness(FitnessCenterDto dto, 
                                @RequestParam(value="imageFile", required=false) MultipartFile imageFile,
                                HttpSession session, HttpServletResponse response) throws IOException {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        
        if (loginUser != null && loginUser.getMemberId() != null) {
            dto.setMemberId(loginUser.getMemberId());
        }

        fitnessService.registerFitness(dto, imageFile);
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script>alert('시설 등록이 요청되었습니다.\\n관리자의 승인 후 리스트에 정상 노출됩니다.'); location.href='/fitness_list';</script>");
        out.flush();
    }

    @GetMapping("/fitness_list")
    public String fitnessList() {
        return "fitness/fitness_list";
    }

    @GetMapping("/api/fitness/data")
    @ResponseBody
    public List<FitnessCenterDto> getFitnessData(
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "dayPass", required = false) Boolean dayPass,
            @RequestParam(value = "is24h", required = false) Boolean is24h) {
        try {
            return fitnessService.searchCenters(location, type, dayPass, is24h);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/fitness_detail")
    public String fitnessDetail(@RequestParam("id") Integer id, Model model, HttpSession session) {
        FitnessCenterDto center = fitnessService.getFitnessCenterById(id);
        model.addAttribute("center", center);
        
        model.addAttribute("bestReviews", fitnessService.getBestReviews(id));
        
        List<CenterTicketDto> tickets = fitnessService.getTicketsByCenterNo(id);
        model.addAttribute("tickets", tickets);
        
        boolean isVerifiedBuyer = false;
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        
        if (loginUser != null && loginUser.getMemberId() != null) {
            Long memberId = loginUser.getMemberId();
            model.addAttribute("loginUserId", memberId);
            model.addAttribute("isRecommended", fitnessService.isRecommended(id, memberId));
            model.addAttribute("isBookmarked", fitnessService.isBookmarked(id, memberId));
            
            Boolean sessionVerified = (Boolean) session.getAttribute("verified_" + id);
            if (sessionVerified != null && sessionVerified) {
                isVerifiedBuyer = true;
            }
        }
        
        model.addAttribute("isVerifiedBuyer", isVerifiedBuyer);
        
        return "fitness/fitness_detail";
    }

    @PostMapping("/api/fitness/recommend")
    @ResponseBody
    public Map<String, Object> toggleRecommend(@RequestParam("centerNo") Integer centerNo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null || loginUser.getMemberId() == null) {
            result.put("success", false);
            return result;
        }
        
        boolean isRecommended = fitnessService.toggleRecommend(centerNo, loginUser.getMemberId());
        result.put("success", true);
        result.put("isRecommended", isRecommended);
        result.put("recommendCount", fitnessService.getRecommendCount(centerNo));
        return result;
    }

    @PostMapping("/api/fitness/bookmark")
    @ResponseBody
    public Map<String, Object> toggleBookmark(@RequestParam("centerNo") Integer centerNo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null || loginUser.getMemberId() == null) {
            result.put("success", false);
            return result;
        }
        
        boolean isBookmarked = fitnessService.toggleBookmark(centerNo, loginUser.getMemberId());
        result.put("success", true);
        result.put("isBookmarked", isBookmarked);
        return result;
    }

    @PostMapping("/api/fitness/review")
    @ResponseBody
    public Map<String, Object> addReview(@RequestBody ReviewDto review, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null || loginUser.getMemberId() == null) {
            result.put("success", false);
            return result;
        }
        
        Boolean isVerified = (Boolean) session.getAttribute("verified_" + review.getCenterNo());
        if (isVerified == null || !isVerified) {
            result.put("success", false);
            result.put("msg", "결제 및 영수증 인증이 필요합니다.");
            return result;
        }
        
        try {
            review.setMemberId(loginUser.getMemberId());
            fitnessService.addReview(review);
            result.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
        }
        return result;
    }
    
    @PostMapping("/api/fitness/verify-receipt")
    @ResponseBody
    public Map<String, Object> verifyReceipt(
            @RequestParam("centerNo") Integer centerNo, 
            @RequestParam("receiptImage") MultipartFile receiptImage,
            HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        
        if (loginUser == null) {
            result.put("success", false);
            result.put("msg", "로그인이 필요합니다.");
            return result;
        }

        try {
            session.setAttribute("verified_" + centerNo, true);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "인증 처리 중 오류가 발생했습니다.");
        }
        return result;
    }

    @GetMapping("/api/fitness/center/{centerNo}/reviews")
    @ResponseBody
    public Map<String, Object> getAllReviews(@PathVariable("centerNo") Integer centerNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ReviewDto> reviewList = fitnessService.getAllReviewsByCenterNo(centerNo);
            result.put("success", true);
            result.put("list", reviewList);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", "리뷰 데이터를 불러오는 중 오류가 발생했습니다.");
        }
        return result;
    }

    // =======================================================================
    // 🌟 [수정] 장바구니 담을 때 시설명(centerName)과 시설번호(centerNo) 추가 수신
    // =======================================================================
    @PostMapping("/api/cart/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam("ticketNo") Integer ticketNo, 
                                         @RequestParam("ticketName") String ticketName, 
                                         @RequestParam("price") Integer price, 
                                         @RequestParam("centerNo") Integer centerNo,
                                         @RequestParam("centerName") String centerName,
                                         HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();
        
        boolean found = false;
        for (Map<String, Object> item : cart) {
            // 동일한 티켓이면 수량만 증가
            if (item.get("ticketNo").equals(ticketNo)) {
                item.put("qty", (Integer)item.get("qty") + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("ticketNo", ticketNo);
            newItem.put("ticketName", ticketName);
            newItem.put("price", price);
            newItem.put("qty", 1);
            // 시설 정보 함께 저장
            newItem.put("centerNo", centerNo);
            newItem.put("centerName", centerName);
            
            cart.add(newItem);
        }
        session.setAttribute("cart", cart); 
        
        result.put("success", true);
        return result;
    }

    @GetMapping("/api/cart/count")
    @ResponseBody
    public Integer getCartCount(HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) return 0;
        return cart.stream().mapToInt(i -> (Integer)i.get("qty")).sum();
    }

    @GetMapping("/api/cart/items")
    @ResponseBody
    public List<Map<String, Object>> getCartItems(HttpSession session) {
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        return cart != null ? cart : new ArrayList<>();
    }

    @PostMapping("/api/cart/change")
    @ResponseBody
    public Map<String, Object> changeCartQty(@RequestParam("ticketNo") Integer ticketNo, 
                                             @RequestParam("delta") Integer delta, 
                                             HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> {
                if (item.get("ticketNo").equals(ticketNo)) {
                    int newQty = (Integer)item.get("qty") + delta;
                    if (newQty <= 0) return true; 
                    item.put("qty", newQty);
                }
                return false;
            });
            session.setAttribute("cart", cart);
        }
        result.put("success", true);
        return result;
    }

    @PostMapping("/api/cart/clear")
    @ResponseBody
    public Map<String, Object> clearCart(HttpSession session) {
        session.removeAttribute("cart");
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @GetMapping("/payment/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        MemberDto loginUser = (MemberDto) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/auth/login";
        }
        
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/fitness_list"; 
        }
        
        int totalPrice = cart.stream().mapToInt(i -> (Integer)i.get("price") * (Integer)i.get("qty")).sum();
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        
        return "fitness/checkout";
    }
}