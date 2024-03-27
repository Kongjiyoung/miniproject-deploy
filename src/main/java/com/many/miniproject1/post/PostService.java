package com.many.miniproject1.post;


import com.many.miniproject1._core.errors.exception.Exception403;
import com.many.miniproject1._core.errors.exception.Exception404;
import com.many.miniproject1._core.common.ProfileImageService;
import com.many.miniproject1.resume.Resume;

import com.many.miniproject1.skill.Skill;
import com.many.miniproject1.skill.SkillJPARepository;
import com.many.miniproject1.skill.SkillResponse;
import com.many.miniproject1.user.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.List;


@RequiredArgsConstructor
@Service
public class PostService {
    private final PostJPARepository postJPARepository;
    private final PostQueryRepository postQueryRepository;
    private final SkillJPARepository skillJPARepository;

    @Transactional
    public void postDelete(int postId, int sessionUserId) {
        Post post = postJPARepository.findById(postId)
                .orElseThrow(() -> new Exception404("공고글을 찾을 수 없습니다"));

        if (sessionUserId != post.getUser().getId()) {
            throw new Exception403("공고글을 삭제할 권한이 없습니다");
        }

        postJPARepository.deleteById(postId);
    }

    @Transactional
    public Post save(PostRequest.PostSaveDTO reqDTO, User sessionUser){
        Post post = postJPARepository.save(reqDTO.toEntity(sessionUser));
        List<Skill> skills = new ArrayList<>();
        for (String skillName : reqDTO.getSkill()) {
            SkillResponse.PostSaveDTO skill = new SkillResponse.PostSaveDTO();
            skill.setSkill(skillName);
            skill.setPost(post);
            skills.add(skill.toEntity());
        }
        skillJPARepository.saveAll(skills);
        return post;
    }

    public List<Post> getResumeList(Integer userId){
        return postJPARepository.findByUserIdJoinSkillAndUser(userId);
    }

    // 공고 상세보기 YSH
    public Post postDetail (PostResponse.PostDetailDTO responseDTO){
        Post post = postJPARepository.findByIdJoinSkillAndCompany(responseDTO.getId());
        return post;
    }
}
