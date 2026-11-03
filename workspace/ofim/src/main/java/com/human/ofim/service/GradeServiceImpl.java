package com.human.ofim.service;

import org.springframework.stereotype.Service;

import com.human.ofim.mapper.GradeMapper;

@Service
public class GradeServiceImpl implements GradeService {

	GradeMapper gradeMapper;

	public GradeServiceImpl(GradeMapper gradeMapper) {
		this.gradeMapper = gradeMapper;
	}

	@Override
	public void updateMembership(long memberId, String planName) {

		int result = gradeMapper.updateMembership(memberId, planName);

		if (result == 0) {
			throw new RuntimeException("멤버십 업데이트 실패.");
		}

	}

}
