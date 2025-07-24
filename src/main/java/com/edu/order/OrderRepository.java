package com.edu.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByMemberAndLecture(Member member, Lecture lecture);
    List<Order> findByMember(Member member);
}
