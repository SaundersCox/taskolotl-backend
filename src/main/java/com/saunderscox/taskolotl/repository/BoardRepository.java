package com.saunderscox.taskolotl.repository;

import com.saunderscox.taskolotl.entity.Board;
import com.saunderscox.taskolotl.entity.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {

  Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  Page<Board> findByBoardType(BoardType boardType, Pageable pageable);

  Page<Board> findByOwnersId(UUID ownerId, Pageable pageable);

  Page<Board> findByMembersId(UUID memberId, Pageable pageable);

  Page<Board> findByOwnersIdOrMembersId(UUID ownerId, UUID memberId, Pageable pageable);

  Page<Board> findByRolesId(UUID roleId, Pageable pageable);

  Page<Board> findBySkillsId(UUID skillId, Pageable pageable);

  Page<Board> findByVisibleTrue(Pageable pageable);

  long countByOwnersId(UUID ownerId);

  boolean existsByTitleIgnoreCase(String title);
}
