package project.web.admin.activity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.*;
import project.mall.LanguageEnum;
import project.mall.activity.dto.ActivityPrizePoolEditDTO;
import project.mall.activity.dto.ActivityPrizePoolShowDTO;
import project.mall.activity.dto.MultiLanguageField;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizePool;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityPrizePoolService;
import project.mall.activity.service.ActivityPrizeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/activityPrize")
@CrossOrigin
public class AdminCommonActivityPrizeController extends BaseAction {

    @Resource
    private ActivityPrizeService activityPrizeService;

    @Resource
    private ActivityPrizePoolService activityPrizePoolService;

    @Resource
    private ActivityLibraryService activityLibraryService;

    @GetMapping("/pageListPrizePool.action")
    public Object pageListPrizePool(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String keyword = request.getParameter("keyword");
        String prizeTypeStr = request.getParameter("prizeType");
        String fromTime = request.getParameter("fromTime");
        String toTime = request.getParameter("toTime");

        int prizeType = 0;
        if (StrUtil.isNotBlank(prizeTypeStr)) {
            prizeType = Integer.parseInt(prizeTypeStr);
        }

        PageInfo pageInfo = getPageInfo(request);
        Page page = activityPrizePoolService.listPrize(keyword, prizeType, -1, fromTime, toTime, pageInfo.getPageNum(), pageInfo.getPageSize());

        List<ActivityPrizePoolShowDTO> pageList = new ArrayList<>();
        for (Object obj : page.getElements()) {
            ActivityPrizePool prizeObj = (ActivityPrizePool) obj;
            ActivityPrizePoolShowDTO onePrizeDto = new ActivityPrizePoolShowDTO();
            BeanUtil.copyProperties(prizeObj, onePrizeDto);
            onePrizeDto.setPrizeName(StrUtil.isBlank(prizeObj.getPrizeNameCn()) ? prizeObj.getPrizeNameEn() : prizeObj.getPrizeNameCn());
            onePrizeDto.setCreateTime(DateUtil.formatDateTime(prizeObj.getCreateTime()));
            onePrizeDto.setUpdateTime(DateUtil.formatDateTime(prizeObj.getUpdateTime()));
            
            pageList.add(onePrizeDto);
        }
        page.setElements(pageList);

        resultObject.setData(page);
        return resultObject;
    }

    @GetMapping("/listAllPrizePool.action")
    public Object listAllPrizePool() {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        List<ActivityPrizePool> results = activityPrizePoolService.listAll();
        resultObject.setData(results);
        return resultObject;
    }

    @GetMapping("/listAllPrize.action")
    public Object listAllPrize(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String activityId = request.getParameter("activityId");

        List<ActivityPrize> results = activityPrizeService.listByActivityId(activityId, -1);
        resultObject.setData(results);
        return resultObject;
    }

    @GetMapping("/prizeDetail.action")
    public Object prizeDetail(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String prizeId = request.getParameter("prizeId");
        if (StrUtil.isBlank(prizeId)) {
            throw new BusinessException("未指定奖品ID");
        }

        ActivityPrizePool activityPrizePool = activityPrizePoolService.detail(prizeId);
        if (activityPrizePool == null) {
            throw new BusinessException("奖品不存在");
        }

        ActivityPrizePoolEditDTO retDto = new ActivityPrizePoolEditDTO();
        BeanUtil.copyProperties(activityPrizePool, retDto);

        List<MultiLanguageField> i18nNames = new ArrayList<>();
        retDto.setI18nNames(i18nNames);
        if (StrUtil.isNotBlank(activityPrizePool.getPrizeNameCn())) {
            MultiLanguageField cnName = new MultiLanguageField();
            cnName.setLang(LanguageEnum.CN.getLang());
            cnName.setContent(activityPrizePool.getPrizeNameCn());

            i18nNames.add(cnName);
        }
        if (StrUtil.isNotBlank(activityPrizePool.getPrizeNameEn())) {
            MultiLanguageField cnName = new MultiLanguageField();
            cnName.setLang(LanguageEnum.EN.getLang());
            cnName.setContent(activityPrizePool.getPrizeNameEn());

            i18nNames.add(cnName);
        }

        resultObject.setData(retDto);
        return resultObject;
    }

    @PostMapping("/savePrizePool.action")
    public Object savePrizePool(HttpServletRequest request, @RequestBody ActivityPrizePoolEditDTO prizePoolInfo) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        // 操作账号
        String operator = request.getParameter("username");
        prizePoolInfo.setCreateBy(operator);

        Date now = new Date();
        ActivityPrizePool prizeEntity = null;
        boolean changed = false;

        if (StrUtil.isBlank(prizePoolInfo.getId()) || Objects.equals(prizePoolInfo.getId(), "0")) {
            prizeEntity = new ActivityPrizePool();
            prizeEntity.setCreateTime(now);
            prizeEntity.setStatus(1);
        } else {
            prizeEntity = activityPrizePoolService.detail(prizePoolInfo.getId());
            if (prizeEntity == null) {
                throw new BusinessException("奖品不存在");
            }
        }
        prizeEntity.setUpdateTime(now);

        prizeEntity.setCreateBy(operator);
        prizeEntity.setImage(prizePoolInfo.getImage());
        prizeEntity.setPrizeType(prizePoolInfo.getPrizeType());
        prizeEntity.setPrizeAmount(prizePoolInfo.getPrizeAmount());
        prizeEntity.setRemark(prizePoolInfo.getRemark());

        List<MultiLanguageField> i18nNames = prizePoolInfo.getI18nNames();
        if (CollectionUtil.isEmpty(i18nNames)) {
            throw new BusinessException("未设置奖品名称");
        }
        for (MultiLanguageField oneLang : i18nNames) {
            if (oneLang.getLang().equalsIgnoreCase(LanguageEnum.CN.getLang())) {
                prizeEntity.setPrizeNameCn(oneLang.getContent());
            } else if (oneLang.getLang().equalsIgnoreCase(LanguageEnum.EN.getLang())) {
                prizeEntity.setPrizeNameEn(oneLang.getContent());
            }
        }

        activityPrizePoolService.save(prizeEntity);

        // TODO 奖品池记录有变更，同步下活动奖品
        //activityPrizeService.updateSyncAttrs(prizeEntity);

        return resultObject;
    }

    @DeleteMapping("/deletePrizePool.action")
    public Object deletePrizePool(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String prizeId = request.getParameter("prizeId");
        if (StrUtil.isBlank(prizeId)) {
            throw new BusinessException("未指定奖品ID");
        }

        activityPrizePoolService.delete(prizeId);
        return resultObject;
    }

    /**
     * 编辑奖品记录时加载奖品信息
     *
     * @param request
     * @return
     */
    @GetMapping("/prizePoolDetail.action")
    public Object prizePoolDetail(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String id = request.getParameter("id");

        ActivityPrizePool detail = activityPrizePoolService.detail(id);
        resultObject.setData(detail);
        return resultObject;
    }


}
