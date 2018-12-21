package com.gioov.nimrod.common.mail.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gioov.common.mybatis.Pageable;
import com.gioov.common.mybatis.Sort;
import com.gioov.nimrod.common.Common;
import com.gioov.nimrod.common.easyui.Pagination;
import com.gioov.nimrod.common.mail.common.MailProducer;
import com.gioov.nimrod.common.mail.entity.MailEntity;
import com.gioov.nimrod.common.mail.mapper.MailMapper;
import com.gioov.nimrod.common.mail.service.MailService;
import com.gioov.nimrod.system.service.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author godcheese
 * @date 2018/2/22
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private MailMapper mailMapper;

    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private MailProducer mailProducer;

    @Autowired
    private Common common;

    @Override
    public void initialize() {

        String host = (String) dictionaryService.get("MAIL", "HOST");
        String protocol = (String) dictionaryService.get("MAIL", "PROTOCOL");
        String port = (String) dictionaryService.get("MAIL", "PORT");
        String username = (String) dictionaryService.get("MAIL", "USERNAME");
        String password = (String) dictionaryService.get("MAIL", "PASSWORD");
        String defaultEncoding = (String) dictionaryService.get("MAIL", "DEFAULT_ENCODING");
        String smtpAuth = (String) dictionaryService.get("MAIL", "SMTP_AUTH");
        String startTlsEnable = (String) dictionaryService.get("MAIL", "STARTTLS_ENABLE");
        String startTlsRequired = (String) dictionaryService.get("MAIL", "STARTTLS_REQUIRED");

        javaMailSender = new JavaMailSenderImpl();
        if (host != null) {
            javaMailSender.setHost(host);
        }
        if (protocol != null) {
            javaMailSender.setProtocol(protocol);
        }
        if (port != null) {
            javaMailSender.setPort(Integer.valueOf(port));
        }
        if (username != null) {
            javaMailSender.setUsername(username);
        }
        if (password != null) {
            javaMailSender.setPassword(password);
        }
        if (password != null) {
            javaMailSender.setPassword(password);
        }
        if (defaultEncoding != null) {
            javaMailSender.setDefaultEncoding(defaultEncoding);
        }

        Properties properties = new Properties();
        if (smtpAuth != null) {
            properties.setProperty("mail.smtp.auth", smtpAuth);
        }
        if (startTlsEnable != null) {
            properties.setProperty("mail.starttls.enable", startTlsEnable);
        }
        if (startTlsRequired != null) {
            properties.setProperty("mail.starttls.required", startTlsRequired);
        }
        javaMailSender.setJavaMailProperties(properties);
    }


//    public void initialize() {
//
////        MailProperties mailProperties;
//
//        javaMailSender = new JavaMailSenderImpl();
//        String host = (String) dictionaryService.get("MAIL", "HOST");
//        if (host != null) {
//            javaMailSender.setHost(host);
//        }
//        String protocol = (String) dictionaryService.get("MAIL", "PROTOCOL");
//        if (protocol != null) {
//            javaMailSender.setProtocol(protocol);
//        }
//        String port = (String) dictionaryService.get("MAIL", "PORT");
//        if (port != null) {
//            javaMailSender.setPort(Integer.valueOf(port));
//        }
//        String username = (String) dictionaryService.get("MAIL", "USERNAME");
//        if (username != null) {
//            javaMailSender.setUsername(username);
//        }
//        String password = (String) dictionaryService.get("MAIL", "PASSWORD");
//        if (password != null) {
//            javaMailSender.setPassword(password);
//        }
//        String defaultEncoding = (String) dictionaryService.get("MAIL", "DEFAULT_ENCODING");
//        if (password != null) {
//            javaMailSender.setPassword(password);
//        }
//        javaMailSender.setDefaultEncoding(defaultEncoding);
//
//        Properties properties = new Properties();
//        String smtpAuth = (String) dictionaryService.get("MAIL", "SMTP_AUTH");
//        if (smtpAuth != null) {
//            properties.setProperty("mail.smtp.auth", smtpAuth);
//        }
//        String startTlsEnable = (String) dictionaryService.get("MAIL", "STARTTLS_ENABLE");
//        if (startTlsEnable != null) {
//            properties.setProperty("mail.starttls.enable", startTlsEnable);
//        }
//        String startTlsRequired = (String) dictionaryService.get("MAIL", "STARTTLS_REQUIRED");
//        if (startTlsRequired != null) {
//            properties.setProperty("mail.starttls.required", startTlsRequired);
//        }
//        javaMailSender.setJavaMailProperties(properties);
//    }

//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public MailEntity addQueue(MailEntity mailEntity) throws BaseResponseException {
//        MailEntity mailEntity1;
//
//        mailEntity1 = insertOne(mailEntity);
//        if (mailEntity1 != null) {
////                EmailQueue.getEmailQueue().produce(mailEntity1);
//        }
////            throw new BaseResponseException("操作失败，邮件队列报错");
//
//        return mailEntity1;
//    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void send(MailEntity mailEntity) {
        String mailSplit = ";";
//        MailEntity mailEntity1 = mailEntity;
        MailEntity mailEntity1 = mailMapper.getOne(mailEntity.getId());
        if (mailEntity1 != null) {
            try {
                mailEntity1.setStatus(Integer.valueOf((String) dictionaryService.get("SMS_STATUS", "FAIL")));
                String to = mailEntity1.getTo();
                if (mailEntity1.getHtml().equals(Integer.valueOf((String) dictionaryService.get("IS_OR_NOT", "IS")))) {
                    if (to.indexOf(mailSplit) > 0) {
                        sendMimeEmailMessage(mailEntity1.getFrom(), to.split(mailSplit), mailEntity1.getSubject(), mailEntity1.getText(), true);
                    } else {
                        sendMimeEmailMessage(mailEntity1.getFrom(), to.split(mailSplit), mailEntity1.getSubject(), mailEntity1.getText(), true);
                    }
                } else {
                    if (to.indexOf(mailSplit) > 0) {
                        sendSimpleEmailMessage(mailEntity1.getFrom(), to.split(mailSplit), mailEntity1.getSubject(), mailEntity1.getText());
                    } else {
                        sendSimpleEmailMessage(mailEntity1.getFrom(), to, mailEntity1.getSubject(), mailEntity1.getText());
                    }
                }
                mailEntity1.setStatus((Integer.valueOf((String) dictionaryService.get("SMS_STATUS", "SUCCESS"))));
                LOGGER.info("send success.");
                mailEntity1.setGmtModified(new Date());
            } catch (Exception e) {
                mailEntity1.setError(e.getMessage());
                mailEntity1.setStatus(Integer.valueOf((String) dictionaryService.get("SMS_STATUS", "FAIL")));
            }
            mailMapper.updateOne(mailEntity1);
        }
    }

    private void sendSimpleEmailMessage(String from, String to, String subject, String text) {
        sendSimpleEmailMessage(from, new String[]{to}, subject, text);
    }

    private void sendSimpleEmailMessage(String from, String[] toArray, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        if (from != null) {
            simpleMailMessage.setFrom(from);
        }
        simpleMailMessage.setTo(toArray);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        javaMailSender.send(simpleMailMessage);
    }

    private void sendSimpleEmailMessage(String from, List<String> toList, String subject, String text) {
        String[] toArray = new String[toList.size()];
        sendSimpleEmailMessage(from, toArray, subject, text);
    }

    private void sendMimeEmailMessage(String from, String[] toArray, String subject, String text, boolean html) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(toArray);
        mimeMessage.setSubject(subject);
        mimeMessageHelper.setText(text, html);
        javaMailSender.send(mimeMessage);
    }

    private void sendMimeEmailMessage(String from, String to, String subject, String text, boolean html) throws MessagingException {
        sendMimeEmailMessage(from, new String[]{to}, subject, text, html);
    }

    private void sendMimeEmailMessage(String from, List<String> toList, String subject, String text, boolean html) throws MessagingException {
        String[] toArray = new String[toList.size()];
        sendMimeEmailMessage(from, toList.toArray(toArray), subject, text, html);
    }

    @Override
    public Pagination.Result<MailEntity> pageAll(Integer page, Integer rows, Sort sort) {
        List<MailEntity> mailEntityList;
        Pagination.Result<MailEntity> paginationResult = new Pagination().new Result<>();
        mailEntityList = mailMapper.pageAll(new Pageable(page, rows, sort));
        if (mailEntityList != null) {
            paginationResult.setRows(mailEntityList);
        }
        int count = mailMapper.countAll();
        paginationResult.setTotal(count);
        return paginationResult;
    }

    /**
     * 设置 from 为数据字典的值
     *
     * @param mailEntity MailEntity
     */
    private void setFrom(MailEntity mailEntity) {
        String from = mailEntity.getFrom();
        if (from == null || "".equals(from)) {
            from = (String) dictionaryService.get("MAIL", "FROM");
            if (from != null) {
                mailEntity.setFrom(from);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public MailEntity insertOne(MailEntity mailEntity) {
        MailEntity mailEntity1 = null;
        try {
            mailEntity1 = new MailEntity();
            Date date = new Date();
            Integer status = mailEntity.getStatus();
            status = status != null ? status : Integer.valueOf((String) dictionaryService.get("SMS_STATUS", "WAIT"));
            mailEntity1.setStatus(status);
            mailEntity1.setFrom(mailEntity.getFrom());
            setFrom(mailEntity1);
            mailEntity1.setTo(mailEntity.getTo());
            mailEntity1.setSubject(mailEntity.getSubject());
            mailEntity1.setText(mailEntity.getText());
            mailEntity1.setHtml(mailEntity.getHtml());
            mailEntity1.setRemark(mailEntity.getRemark());
            mailEntity1.setGmtModified(date);
            mailEntity1.setGmtCreated(date);
            mailMapper.insertOne(mailEntity1);
            mailProducer.produce(common.objectToJson(mailEntity1));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return mailEntity1;
    }

    @Override
    public int deleteAll(List<Long> idList) {
        return mailMapper.deleteAll(idList);
    }

    @Override
    public MailEntity getOne(Long id) {
        return mailMapper.getOne(id);
    }

}
