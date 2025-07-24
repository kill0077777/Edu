package com.edu.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.entity.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    // 주문(결제) 생성
    public Order createOrder(Member member, Lecture lecture) {
        // 이미 주문했는지 체크(중복 방지)
        if (orderRepository.existsByMemberAndLecture(member, lecture)) {
            throw new RuntimeException("이미 결제된 강의입니다.");
        }
        Order order = Order.builder()
                .member(member)
                .lecture(lecture)
                .price(lecture.getPrice())
                .status("PAID")
                .build();
        return orderRepository.save(order);
    }

    // 회원의 결제/수강 강의 목록
    public List<Order> getOrdersByMember(Member member) {
        return orderRepository.findByMember(member);
    }

    public boolean isLecturePaid(Member member, Lecture lecture) {
        return orderRepository.existsByMemberAndLecture(member, lecture);
    }
}

