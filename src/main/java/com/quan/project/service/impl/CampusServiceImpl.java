package com.quan.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quan.project.common.CurrentUser;
import com.quan.project.dto.PageDTO;
import com.quan.project.entity.Campus;
import com.quan.project.exception.BusinessException;
import com.quan.project.mapper.CampusMapper;
import com.quan.project.service.CampusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 校区服务实现类
 */
@Service
public class CampusServiceImpl implements CampusService {
    
    private static final Logger log = LoggerFactory.getLogger(CampusServiceImpl.class);
    
    @Autowired
    private CampusMapper campusMapper;
    
    @Override
    public Campus createCampus(Campus campus) {
        try {
            // 验证校区名称是否已存在
            Campus existingCampus = campusMapper.selectByName(campus.getName());
            if (existingCampus != null) {
                throw new BusinessException("校区名称已存在");
            }
            
            // 插入校区
            int result = campusMapper.insert(campus);
            if (result <= 0) {
                throw new BusinessException("创建校区失败");
            }
            
            log.info("创建校区成功，校区ID: {}, 创建者: {}", campus.getId(), CurrentUser.getCurrentUserId());
            return campus;
        } catch (Exception e) {
            log.error("创建校区失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("创建校区失败");
        }
    }
    
    @Override
    public void deleteCampus(Integer id) {
        try {
            // 检查校区是否存在
            Campus campus = campusMapper.selectById(id);
            if (campus == null) {
                throw new BusinessException("校区不存在");
            }
            
            // TODO: 检查校区是否有关联数据（用户、球台等）
            
            int result = campusMapper.deleteById(id);
            if (result <= 0) {
                throw new BusinessException("删除校区失败");
            }
            
            log.info("删除校区成功，校区ID: {}, 操作者: {}", id, CurrentUser.getCurrentUserId());
        } catch (Exception e) {
            log.error("删除校区失败，校区ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("删除校区失败");
        }
    }
    
    @Override
    public void updateCampus(Campus campus) {
        try {
            // 检查校区是否存在
            Campus existingCampus = campusMapper.selectById(campus.getId());
            if (existingCampus == null) {
                throw new BusinessException("校区不存在");
            }
            
            // 如果修改了校区名称，检查新名称是否已存在
            if (!existingCampus.getName().equals(campus.getName())) {
                Campus duplicateCampus = campusMapper.selectByName(campus.getName());
                if (duplicateCampus != null && !duplicateCampus.getId().equals(campus.getId())) {
                    throw new BusinessException("校区名称已存在");
                }
            }
            
            int result = campusMapper.update(campus);
            if (result <= 0) {
                throw new BusinessException("更新校区失败");
            }
            
            log.info("更新校区成功，校区ID: {}, 操作者: {}", campus.getId(), CurrentUser.getCurrentUserId());
        } catch (Exception e) {
            log.error("更新校区失败，校区ID: {}", campus.getId(), e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("更新校区失败");
        }
    }
    
    @Override
    public Campus getCampusById(Integer id) {
        try {
            Campus campus = campusMapper.selectById(id);
            if (campus == null) {
                throw new BusinessException("校区不存在");
            }
            return campus;
        } catch (Exception e) {
            log.error("查询校区失败，校区ID: {}", id, e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("查询校区失败");
        }
    }
    
    @Override
    public List<Campus> getAllCampuses() {
        try {
            return campusMapper.selectAll();
        } catch (Exception e) {
            log.error("查询所有校区失败", e);
            throw new BusinessException("查询校区列表失败");
        }
    }
    
    @Override
    public PageInfo<Campus> getCampusPage(PageDTO pageRequest) {
        try {
            // 设置分页参数
            PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
            
            // 执行动态条件查询
            List<Campus> campuses = campusMapper.selectPageByConditions(pageRequest);
            
            // 直接返回PageInfo
            return new PageInfo<>(campuses);
        } catch (Exception e) {
            log.error("分页查询校区失败", e);
            throw new BusinessException("分页查询校区失败");
        }
    }
    
    @Override
    public Campus getCampusByManagerId(Integer managerId) {
        try {
            return campusMapper.selectByManagerId(managerId);
        } catch (Exception e) {
            log.error("根据管理员ID查询校区失败，管理员ID: {}", managerId, e);
            throw new BusinessException("查询校区失败");
        }
    }
    
    @Override
    public Campus getCampusByName(String name) {
        try {
            return campusMapper.selectByName(name);
        } catch (Exception e) {
            log.error("根据名称查询校区失败，校区名称: {}", name, e);
            throw new BusinessException("查询校区失败");
        }
    }
}
