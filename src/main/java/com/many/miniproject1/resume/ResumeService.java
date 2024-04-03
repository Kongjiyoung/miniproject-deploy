package com.many.miniproject1.resume;

import com.many.miniproject1._core.common.ProfileImageSaveUtil;
import com.many.miniproject1._core.errors.exception.Exception404;
import com.many.miniproject1.apply.ApplyJPARepository;
import com.many.miniproject1.offer.OfferJPARepository;
import com.many.miniproject1.scrap.Scrap;
import com.many.miniproject1.scrap.ScrapJPARepository;
import com.many.miniproject1.skill.Skill;
import com.many.miniproject1.skill.SkillJPARepository;

import com.many.miniproject1.skill.SkillResponse;

import com.many.miniproject1.user.User;
import com.many.miniproject1.user.UserJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ResumeService {
    private final ResumeJPARepository resumeJPARepository;
    private final SkillJPARepository skillJPARepository;
    private final ApplyJPARepository applyJPARepository;
    private final OfferJPARepository offerJPARepository;
    private final ScrapJPARepository scrapJPARepository;

    private final UserJPARepository userJPARepository;

    // 이력서 목록
    public List<ResumeResponse.ResumeListDTO> findResumeList(Integer userId) {
        List<Resume> resumeList = resumeJPARepository.findByUserIdJoinSkillAndUser(userId);
        return resumeList.stream().map(resume -> new ResumeResponse.ResumeListDTO(resume)).toList();
    }


    // 이력서 작성
    @Transactional
    public ResumeResponse.ResumeSaveDTO save(ResumeRequest.SaveDTO reqDTO, User sessionUser) {

        Resume resume = resumeJPARepository.save(reqDTO.toEntity(sessionUser));

        List<Skill> skills = new ArrayList<>();
        for (String skillName : reqDTO.getSkills()) {
            SkillResponse.SaveDTO skill = new SkillResponse.SaveDTO();
            skill.setResume(resume);
            skill.setSkill(skillName);
            skills.add(skill.toEntity());
        }

        List<Skill> skillList = skillJPARepository.saveAll(skills);

        return new ResumeResponse.ResumeSaveDTO(resume, skillList);
    }

    // 이력서 수정
    @Transactional
    public ResumeResponse.UpdateDTO update(int resumeId, ResumeRequest.UpdateDTO requestDTO) {

        Resume resume = resumeJPARepository.findById(resumeId)
                .orElseThrow(() -> new Exception404("이력서를 찾을 수 없습니다"));

        resume.setTitle(requestDTO.getTitle());
        resume.setPortfolio(requestDTO.getPortfolio());
        resume.setIntroduce(requestDTO.getIntroduce());
        resume.setCareer(requestDTO.getCareer());
        resume.setSimpleIntroduce(requestDTO.getSimpleIntroduce());
        resume.setProfile(ProfileImageSaveUtil.save(requestDTO.getProfile()));

        // 스킬 수정
        List<Skill> skills = skillJPARepository.findSkillsByResumeId(resume.getId());
        for (Skill skill : skills) {
            skillJPARepository.deleteSkillsByResumeId(resume.getId());
        }

        List<Skill> skills1 = new ArrayList<>();
        for (String skillName : requestDTO.getSkills()) {
            SkillResponse.SaveDTO skill = new SkillResponse.SaveDTO();
            skill.setResume(resume);
            skill.setSkill(skillName);
            skills1.add(skill.toEntity());
        }

        List<Skill> skillList = skillJPARepository.saveAll(skills1);

        return new ResumeResponse.UpdateDTO(resume, skillList);
    }

    public ResumeResponse.UpdateFormDTO findByResume(int resumeId) {
        Resume resume = resumeJPARepository.findById(resumeId)
                .orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다"));

        return new ResumeResponse.UpdateFormDTO(resume);
    }


    public ResumeResponse.ResumeDetailDTO getResumeDetail(int resumeId) {
        Resume resume = resumeJPARepository.findByIdJoinSkillAndUser(resumeId);
        return new ResumeResponse.ResumeDetailDTO(resume);
    }
//    public Resume getResumeSkill(ResumeResponse.DetailDTO respDTO) {
//        ResumeRequest.ResumeDTO resumeDTO = new ResumeRequest.ResumeDTO();
//        List<Resume> resumeList = findResumeList(resumeDTO.getId());
//        ArrayList<ResumeResponse.DetailSkillDTO> resumeSkillList = new ArrayList<>();
//        for (int i = 0; i < resumeList.size(); i++) {
//            List<Skill> skills = skillJPARepository.findSkillsByResumeId(resumeList.get(i).getId());
//            Resume resume = resumeList.get(i);
//            resumeSkillList.add(new ResumeResponse.DetailSkillDTO(resume, skills));
//        }
//        return resumeJPARepository.findByIdJoinSkill(respDTO.getId());

//    }

    @Transactional
    public void deleteResume(Integer resumeId) {
        applyJPARepository.deleteByResumeId(resumeId);
        resumeJPARepository.deleteById(resumeId);
        offerJPARepository.deleteByResumeId(resumeId);
        scrapJPARepository.deleteByResumeId(resumeId);
        skillJPARepository.deleteSkillsByResumeId(resumeId);
    }

    public List<Resume> getResumeFindBySessionUserId(Integer sessionUserId) {
        List<Resume> resumeList = resumeJPARepository.findBySessionUserId(sessionUserId);
        return resumeList;
    }
}
