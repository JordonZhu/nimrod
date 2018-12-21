package com.gioov.nimrod.common.mail.api;

import com.gioov.common.mybatis.Sort;
import com.gioov.common.web.exception.BaseResponseException;
import com.gioov.nimrod.common.constant.Api;
import com.gioov.nimrod.common.easyui.Pagination;
import com.gioov.nimrod.common.mail.entity.MailEntity;
import com.gioov.nimrod.common.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gioov.nimrod.user.service.UserService.SYSTEM_ADMIN;

/**
 * @author godcheese
 * @date 2018/2/22
 */
@RestController
@RequestMapping(Api.System.MAIL)
public class MailRestController {

    private static final String MAIL = "/API/SYSTEM/MAIL";

    @Autowired
    private MailService mailService;

//    @Autowired
//    private MailProducer mailProducer;

    /**
     * 分页获取所有邮件队列
     *
     * @param page 页
     * @param rows 每页显示数量
     * @return ResponseEntity<Pagination.Result                               <                               MailEntity>>
     */
    @PreAuthorize("hasRole('" + SYSTEM_ADMIN + "') OR hasAuthority('" + MAIL + "/PAGE_ALL_QUEUE')")
    @GetMapping(value = "/page_all")
    public ResponseEntity<Pagination.Result<MailEntity>> pageAll(@RequestParam Integer page, @RequestParam Integer rows) {
        Sort sort = new Sort(Sort.Direction.DESC, "gmt_created");
        return new ResponseEntity<>(mailService.pageAll(page, rows, sort), HttpStatus.OK);
    }

    /**
     * 新增邮件
     *
     * @param from    发件人电子邮箱
     * @param to      收件人电子邮箱
     * @param subject 电子邮件主题
     * @param text    电子邮件文本内容
     * @param html    电子邮件是否为 html
     * @param remark  备注
     * @return ResponseEntity<MailEntity>
     */
    @PreAuthorize("hasRole('" + SYSTEM_ADMIN + "') OR hasAuthority('" + MAIL + "/ADD_ONE')")
    @PostMapping(value = "/add_one")
    public ResponseEntity<MailEntity> addOne(@RequestParam String from, @RequestParam String to, @RequestParam String subject, @RequestParam String text, @RequestParam Integer html, @RequestParam String remark) throws BaseResponseException {
        MailEntity mailEntity = new MailEntity();
        mailEntity.setFrom(from);
        mailEntity.setTo(to);
        mailEntity.setSubject(subject);
        mailEntity.setText(text);
        mailEntity.setHtml(html);
        mailEntity.setRemark(remark);
        MailEntity mailEntity1 = mailService.insertOne(mailEntity);
        return new ResponseEntity<>(mailEntity1, HttpStatus.OK);
    }

    /**
     * 指定邮件 id ，获取邮件信息
     *
     * @param id 电子邮件 id
     * @return ResponseEntity<MailEntity>
     */
    @PreAuthorize("hasRole('" + SYSTEM_ADMIN + "') OR hasAuthority('" + MAIL + "/ONE')")
    @GetMapping(value = "/one/{id}")
    public ResponseEntity<MailEntity> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(mailService.getOne(id), HttpStatus.OK);
    }

    /**
     * 指定队列邮件 id ，批量删除队列邮件
     *
     * @param idList 电子邮件 id list
     * @return ResponseEntity<Integer>
     */
    @PreAuthorize("hasRole('" + SYSTEM_ADMIN + "') OR hasAuthority('" + MAIL + "/DELETE_ALL')")
    @PostMapping(value = "/delete_all")
    public ResponseEntity<Integer> deleteAll(@RequestParam("id[]") List<Long> idList) {
        return new ResponseEntity<>(mailService.deleteAll(idList), HttpStatus.OK);
    }

}
