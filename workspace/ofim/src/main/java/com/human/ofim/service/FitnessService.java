package com.human.ofim.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.human.ofim.dto.CenterTicketDto;
import com.human.ofim.dto.FitnessCenterDto;
import com.human.ofim.dto.ReviewDto;
import com.human.ofim.mapper.FitnessMapper;

@Service
public class FitnessService {

    @Autowired
    private FitnessMapper fitnessMapper;

    private final String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/uploads/fitness/";

    @Transactional
    public void registerFitness(FitnessCenterDto dto, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            try {
                File target = new File(uploadPath);
                if(!target.exists()) target.mkdirs();
                imageFile.transferTo(new File(uploadPath + fileName));
                dto.setImageUrl("/uploads/fitness/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fitnessMapper.insertFitnessCenter(dto);
        
        if(dto.getIs24h() == null) dto.setIs24h(false);
        if(dto.getWeekdayOpen() == null || dto.getWeekdayOpen().isEmpty()) dto.setWeekdayOpen("00:00");
        if(dto.getWeekdayClose() == null || dto.getWeekdayClose().isEmpty()) dto.setWeekdayClose("00:00");
        if(dto.getSatOpen() == null || dto.getSatOpen().isEmpty()) dto.setSatOpen("00:00");
        if(dto.getSatClose() == null || dto.getSatClose().isEmpty()) dto.setSatClose("00:00");
        if(dto.getSunOpen() == null || dto.getSunOpen().isEmpty()) dto.setSunOpen("00:00");
        if(dto.getSunClose() == null || dto.getSunClose().isEmpty()) dto.setSunClose("00:00");
        if(dto.getHolidayOpen() == null || dto.getHolidayOpen().isEmpty()) dto.setHolidayOpen("00:00");
        if(dto.getHolidayClose() == null || dto.getHolidayClose().isEmpty()) dto.setHolidayClose("00:00");
        
        fitnessMapper.insertWorkingHours(dto);

        if (dto.getTicketNames() != null && !dto.getTicketNames().isEmpty()) {
            List<CenterTicketDto> tickets = new ArrayList<>();
            for (int i = 0; i < dto.getTicketNames().size(); i++) {
                String tName = dto.getTicketNames().get(i);
                if (tName == null || tName.trim().isEmpty()) continue;

                CenterTicketDto ticket = new CenterTicketDto();
                ticket.setCenterNo(dto.getCenterNo()); 
                ticket.setTicketName(tName);
                
                Integer price = (dto.getTicketPrices() != null && dto.getTicketPrices().size() > i && dto.getTicketPrices().get(i) != null) ? dto.getTicketPrices().get(i) : 0;
                Integer discount = (dto.getDiscountPrices() != null && dto.getDiscountPrices().size() > i && dto.getDiscountPrices().get(i) != null) ? dto.getDiscountPrices().get(i) : 0;
                Integer period = (dto.getPeriodMonths() != null && dto.getPeriodMonths().size() > i && dto.getPeriodMonths().get(i) != null) ? dto.getPeriodMonths().get(i) : 0;

                ticket.setTicketPrice(price);
                ticket.setDiscountPrice(discount);
                ticket.setPeriodMonths(period);

                tickets.add(ticket);
            }
            if (!tickets.isEmpty()) {
                fitnessMapper.insertCenterTickets(tickets); 
            }
        }
    }

    public List<FitnessCenterDto> searchCenters(String keyword, String type, Boolean dayPassYn, Boolean is24h) {
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<FitnessCenterDto> allCenters = fitnessMapper.findAll(kw, type, dayPassYn, is24h);
        allCenters.forEach(this::setRealUiData);
        return allCenters;
    }
    
    public List<FitnessCenterDto> getPremiumBanners() {
        List<FitnessCenterDto> banners = fitnessMapper.selectPremiumBanners();
        banners.forEach(this::setRealUiData); 
        return banners;
    }

    public FitnessCenterDto getFitnessCenterById(Integer id) {
        FitnessCenterDto center = fitnessMapper.findById(id);
        if (center != null) setRealUiData(center);
        return center;
    }

    private void setRealUiData(FitnessCenterDto center) {
        if (center.getImageUrl() == null || center.getImageUrl().isEmpty()) {
            String type = center.getType() != null ? center.getType() : "기타";
            if (type.contains("헬스")) {
                center.setImageUrl("https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=1470&auto=format&fit=crop");
            } else if (type.contains("무도") || type.contains("크로스핏")) {
                center.setImageUrl("https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?q=80&w=1469&auto=format&fit=crop");
            } else if (type.contains("필라테스") || type.contains("요가")) {
                center.setImageUrl("https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=1470&auto=format&fit=crop");
            } else {
                center.setImageUrl("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?q=80&w=1470&auto=format&fit=crop");
            }
        }
        center.setStatusColor(center.getType() != null && center.getType().contains("헬스") ? "bg-primary" : "bg-success");
        center.setRecommendCount(fitnessMapper.getRecommendCount(center.getCenterNo()));
    }

    public List<CenterTicketDto> getTicketsByCenterNo(Integer centerNo) {
        return fitnessMapper.selectTicketsByCenterNo(centerNo);
    }

    @Transactional
    public void addReview(ReviewDto review) {
        fitnessMapper.insertReview(review);
        fitnessMapper.updateCenterReviewStats(review.getCenterNo());
    }

    public List<ReviewDto> getBestReviews(Integer centerNo) {
        return fitnessMapper.selectBestReviews(centerNo);
    }
    
    public List<ReviewDto> getAllReviewsByCenterNo(Integer centerNo) {
        return fitnessMapper.selectAllReviewsByCenterNo(centerNo);
    }

    public List<ReviewDto> getMyReviewsPaging(Long memberId, String keyword, int page, int size) {
        int startRow = (page - 1) * size;
        return fitnessMapper.selectMyReviewsPaging(memberId, keyword, startRow, size);
    }

    public int countMyReviewsPaging(Long memberId, String keyword) {
        return fitnessMapper.countMyReviewsPaging(memberId, keyword);
    }
    
    public boolean deleteReview(int reviewId, Long memberId) {
        return fitnessMapper.deleteReview(reviewId, memberId) > 0;
    }
    
    public List<Map<String, Object>> getMyBookmarkedCenters(Long memberId, String keyword, int page, int amount) {
        int startRow = (page - 1) * amount; 
        return fitnessMapper.selectMyBookmarkedCenters(memberId, keyword, startRow, amount);
    }

    public int countMyBookmarkedCenters(Long memberId, String keyword) {
        return fitnessMapper.countMyBookmarkedCenters(memberId, keyword);
    }

    @Transactional
    public boolean toggleRecommend(Integer centerNo, Long memberId) {
        if (fitnessMapper.checkRecommend(centerNo, memberId) > 0) {
            fitnessMapper.deleteRecommend(centerNo, memberId);
            return false; 
        } else {
            fitnessMapper.insertRecommend(centerNo, memberId);
            return true;
        }
    }
    
    @Transactional
    public boolean toggleBookmark(Integer centerNo, Long memberId) {
        if (fitnessMapper.checkBookmark(centerNo, memberId) > 0) {
            fitnessMapper.deleteBookmark(centerNo, memberId);
            return false;
        } else {
            fitnessMapper.insertBookmark(centerNo, memberId);
            return true;
        }
    }

    public List<ReviewDto> getPartnerReviewsPaging(Long partnerId, int page, int size) {
        int startRow = (page - 1) * size;
        return fitnessMapper.selectReviewsByPartnerPaging(partnerId, startRow, size);
    }

    public int countPartnerReviews(Long partnerId) {
        return fitnessMapper.countReviewsByPartner(partnerId);
    }

    @Transactional
    public boolean updateReviewReply(int reviewId, String replyContent) {
        return fitnessMapper.updateReviewReply(reviewId, replyContent) > 0;
    }

    public int getRecommendCount(Integer centerNo) { return fitnessMapper.getRecommendCount(centerNo); }
    public boolean isRecommended(Integer centerNo, Long memberId) { return fitnessMapper.checkRecommend(centerNo, memberId) > 0; }
    public boolean isBookmarked(Integer centerNo, Long memberId) { return fitnessMapper.checkBookmark(centerNo, memberId) > 0; }
}