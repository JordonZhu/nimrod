package com.gioov.nimrod.system.service.impl;

import com.gioov.common.mybatis.Pageable;
import com.gioov.common.web.exception.BaseResponseException;
import com.gioov.nimrod.common.constant.FailureMessage;
import com.gioov.nimrod.common.easyui.Pagination;
import com.gioov.nimrod.system.entity.DictionaryCategoryEntity;
import com.gioov.nimrod.system.entity.DictionaryEntity;
import com.gioov.nimrod.system.mapper.DictionaryCategoryMapper;
import com.gioov.nimrod.system.mapper.DictionaryMapper;
import com.gioov.nimrod.system.service.DictionaryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author godcheese
 * @date 2018/2/22
 */
@Service
public class DictionaryCategoryServiceImpl implements DictionaryCategoryService {

    @Autowired
    private DictionaryCategoryMapper dictionaryCategoryMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Override
    public Pagination.Result<DictionaryCategoryEntity> pageAllParent(Integer page, Integer rows) {
        List<DictionaryCategoryEntity> dictionaryCategoryEntityList;
        Pagination.Result<DictionaryCategoryEntity> paginationResult = new Pagination().new Result<>();
        dictionaryCategoryEntityList = dictionaryCategoryMapper.pageAllByParentIdIsNull(new Pageable(page, rows));
        if (dictionaryCategoryEntityList != null) {
            paginationResult.setRows(dictionaryCategoryEntityList);
        }
        int count = dictionaryCategoryMapper.countAllByParentIdIsNull();
        paginationResult.setTotal(count);
        return paginationResult;
    }

    @Override
    public List<DictionaryCategoryEntity> listAllByParentId(Long parentId) {
        return dictionaryCategoryMapper.listAllByParentId(parentId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DictionaryCategoryEntity insertOne(DictionaryCategoryEntity dictionaryCategoryEntity) {
        DictionaryCategoryEntity dictionaryCategoryEntity1 = new DictionaryCategoryEntity();
        Date date = new Date();
        dictionaryCategoryEntity1.setName(dictionaryCategoryEntity.getName());
        dictionaryCategoryEntity1.setParentId(dictionaryCategoryEntity.getParentId());
        dictionaryCategoryEntity1.setSort(dictionaryCategoryEntity.getSort());
        dictionaryCategoryEntity1.setRemark(dictionaryCategoryEntity.getRemark());
        dictionaryCategoryEntity1.setGmtModified(date);
        dictionaryCategoryEntity1.setGmtCreated(date);
        dictionaryCategoryMapper.insertOne(dictionaryCategoryEntity1);
        return dictionaryCategoryEntity1;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DictionaryCategoryEntity updateOne(DictionaryCategoryEntity dictionaryCategoryEntity) {
        Date date = new Date();
        DictionaryCategoryEntity dictionaryCategoryEntity1 = dictionaryCategoryMapper.getOne(dictionaryCategoryEntity.getId());
        dictionaryCategoryEntity1.setName(dictionaryCategoryEntity.getName());
        dictionaryCategoryEntity1.setSort(dictionaryCategoryEntity.getSort());
        dictionaryCategoryEntity1.setRemark(dictionaryCategoryEntity.getRemark());
        dictionaryCategoryEntity1.setGmtModified(date);
        dictionaryCategoryMapper.updateOne(dictionaryCategoryEntity1);
        return dictionaryCategoryEntity1;
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public int deleteAll(List<Long> idList) throws BaseResponseException {
        int result = 0;
        for (Long id : idList) {
            DictionaryCategoryEntity dictionaryCategoryEntity = dictionaryCategoryMapper.getOneByParentId(id);
            if (dictionaryCategoryEntity != null) {
                throw new BaseResponseException(FailureMessage.DELETE_DICTIONARY_CATEGORY_FAIL1);
            }
            DictionaryEntity dictionaryEntity = dictionaryMapper.getOneByDictionaryCategoryId(id);
            if (dictionaryEntity != null) {
                throw new BaseResponseException(FailureMessage.DELETE_DICTIONARY_CATEGORY_FAIL2);
            }
            dictionaryCategoryMapper.deleteOne(id);
            result++;
        }
        return result;
    }

    @Override
    public DictionaryCategoryEntity getOne(Long id) {
        return dictionaryCategoryMapper.getOne(id);
    }

}
