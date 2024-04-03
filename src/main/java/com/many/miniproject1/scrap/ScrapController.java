package com.many.miniproject1.scrap;

import com.many.miniproject1.apply.Apply;
import com.many.miniproject1.apply.ApplyResponse;
import com.many.miniproject1.offer.OfferResponse;
import com.many.miniproject1.post.Post;
import com.many.miniproject1.offer.Offer;
import com.many.miniproject1.resume.Resume;
import com.many.miniproject1.resume.ResumeService;
import com.many.miniproject1.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ScrapController {
    private final HttpSession session;
    private final ScrapService scrapService;
    private final ResumeService resumeService;

    //개인 채용 공고 스크랩
    @GetMapping("/person/scrap")
    public String personScrap(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        List<ScrapResponse.ScrapPostListDTO> scrapList = scrapService.personScrapList(sessionUser.getId());
        request.setAttribute("scrapList", scrapList);
        return "person/scrap";
    }

    @GetMapping("/person/scrap/{id}/detail")
    public String personScrapDetailForm(@PathVariable Integer id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        ScrapResponse.ScrapPostDetailDTO scrap = scrapService.scrapPostDetail(id);
        //이력서 선택
        List<Resume> resumeList = scrapService.personResumeList(sessionUser.getId());
        request.setAttribute("scrap", scrap);
        request.setAttribute("resumeList", resumeList);

        return "person/post-scrap-detail";
    }

    @PostMapping("/person/scrap/{id}/detail/delete")
    public String personScrapDelete(@PathVariable Integer id) {
        scrapService.deleteScrapPost(id);
        return "redirect:/person/scrap";
    }

    @PostMapping("/person/scrap/{id}/detail/apply")
    public String personPostApply(@PathVariable Integer id, Integer resumeChoice) { // 스크랩 아이디와 이력서 아이디를 받아서
        scrapService.sendResumeToPost(id, resumeChoice);
        return "redirect:/person/scrap/"+id+"/detail";
    }

    //기업 이력서 스크랩
    @GetMapping("/company/scrap")
    public String companyScrapForm(HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        List<ScrapResponse.ScrapResumeListDTO> scrapList = scrapService.companyScrapList(sessionUser.getId());
        request.setAttribute("scrapList", scrapList);
        return "company/scrap";
    }

    @GetMapping("/company/scrap/{id}/detail")
    public String companyScrapDetailForm(@PathVariable Integer id, HttpServletRequest request) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        ScrapResponse.ScrapResumeDetailDTO scrap = scrapService.getResumeDetail(id);
        List<Post> postList = scrapService.companyPostList(sessionUser.getId());
        request.setAttribute("postList", postList);
        request.setAttribute("scrap", scrap);
        return "company/resume-scrap-detail";
    }

    @PostMapping("/company/scrap/{id}/detail/delete")
    public String companyScrapDelete(@PathVariable Integer id) {
        scrapService.deleteScrap(id);
        return "redirect:/company/scrap";
    }

    @PostMapping("/company/scrap/{id}/detail/offer")
    public String companyResumeOffer(@PathVariable Integer id, Integer postChoice) {
        scrapService.sendPostToResume(id, postChoice);
        return "redirect:/company/scrap/"+id+"/detail";
    }

}