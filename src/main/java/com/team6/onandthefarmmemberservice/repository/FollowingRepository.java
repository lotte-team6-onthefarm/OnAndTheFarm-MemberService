package com.team6.onandthefarmmemberservice.repository;

import com.team6.onandthefarmmemberservice.entity.following.Following;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends CrudRepository<Following, Long> {
	Optional<Following> findByFollowingMemberIdAndFollowerMemberId(
			Long followingMemberId, Long followerMemberId);

	@Query("select f from Following f where f.followerMemberId =:followerId")
	List<Following> findFollowingIdByFollowerId(@Param("followerId")Long followerId);

	@Query("select f from Following f where f.followingMemberId =:followingId")
	List<Following> findFollowerIdByFollowingId(@Param("followingId")Long followingId);
}

