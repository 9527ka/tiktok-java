package project.web.admin.activity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.service.TransactionMethodFragmentFun;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.*;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.ActivityHandlerLoader;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.dto.ActivityEditInfoDTO;
import project.mall.activity.dto.ActivityPrizeDTO;
import project.mall.activity.dto.ActivityShowDTO;
import project.mall.activity.handler.ActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityTemplate;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.service.*;
import project.monitor.activity.Activity;
import util.concurrent.gofun.core.FunParams;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activity")
@CrossOrigin
public class AdminCommonActivityController extends PageActionSupport {

    @Resource
    private ActivityTemplateService activityTemplateService;

    @Resource
    private ActivityLibraryService activityLibraryService;

    @Resource
    private ActivityConfigLogService activityConfigLogService;

    @Resource
    private ActivityPrizeService activityPrizeService;

    @Resource
    private TransactionMethodFragmentFun transactionMethodFragmentFun;

    @Resource
    private ActivityUserPointsService activityUserPointsService;

    @PostMapping("/updateShow.action")
    public Object updateShow(@RequestParam Integer show, @RequestParam String id) {
        ResultObject resultObject = new ResultObject();

        ActivityLibrary activityEntity = activityLibraryService.findById(id);
        if (activityEntity == null) {
            resultObject.setCode("-1");
            resultObject.setMsg("活动记录不存在");
            return resultObject;
        }

        List<ActivityLibrary> showActivityList = activityLibraryService.getShowActivity(activityEntity.getType());
        //List<String> activityIdList = new ArrayList<>();
        for (ActivityLibrary oneActivity : showActivityList) {
            //activityIdList.add(oneActivity.getId().toString());
            if (show == 1) {
                if (!Objects.equals(id, oneActivity.getId().toString())) {
                    resultObject.setCode("-1");
                    resultObject.setMsg("已存在其他处于展示状态的活动，你需要手动关闭其他活动的展示状态");
                    return resultObject;
                }
            }
        }

        try {
            activityLibraryService.updateShow(id, show);
        } catch (BusinessException e) {
            resultObject.setCode("-1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e) {
            logger.error("---> 更新活动的显示状态报错: ", e);
            resultObject.setCode("-1");
            resultObject.setMsg("更新失败");
            return resultObject;
        }

        resultObject.setMsg("success");
        return resultObject;
    }

    @GetMapping("/listActivityType.action")
    public Object listActivityType(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        // 账号
        String operator = request.getParameter("username");

        List<Map<String, Object>> typeList = new ArrayList();
        List<ActivityTemplate> allActivityTemplateList = activityTemplateService.listAllValidActivityType();
        for (ActivityTemplate oneTemplate : allActivityTemplateList) {
            Map<String, Object> oneType = new HashMap<>();
            oneType.put("id", oneTemplate.getId());
            oneType.put("title", oneTemplate.getTitle());
            oneType.put("type", oneTemplate.getType());
            typeList.add(oneType);
        }

        resultObject.setData(typeList);
        return resultObject;
    }

    @GetMapping("/loadActivityConfig.action")
    public Object loadActivityConfig(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String activityTemplateId = request.getParameter("templateId");
        if (StrUtil.isBlank(activityTemplateId)) {
            throw new BusinessException("未指定活动类型");
        }

        ActivityTemplate activityTemplate = activityTemplateService.getById(activityTemplateId);
        if (activityTemplate == null) {
            throw new BusinessException("活动类型记录不存在");
        }

        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityTemplate.getType());
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);
        ActivityEditInfoDTO retDto = handler.loadActivityTemplate(activityTemplate.getId().toString());

        resultObject.setData(retDto);
        return resultObject;
    }

    @GetMapping("/pageList.action")
    public Object pageList(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String keyword = request.getParameter("keyword");
        String statusStr = request.getParameter("status");
        String startTime = request.getParameter("fromTime");
        String endTime = request.getParameter("toTime");

        PageInfo pageInfo = getPageInfo(request);
        Integer status = null;
        if (StrUtil.isNotBlank(statusStr)) {
            status = Integer.parseInt(statusStr);
        }

        Page page = activityLibraryService.listActivity(keyword, status, startTime, endTime, pageInfo.getPageNum(), pageInfo.getPageSize());
        List<ActivityLibrary> pageList = (List<ActivityLibrary>) page.getElements();
        List<ActivityShowDTO> activityDtoList = new ArrayList<>();
        for (ActivityLibrary oneActivity : pageList) {
            ActivityShowDTO oneDto = new ActivityShowDTO();
            BeanUtil.copyProperties(oneActivity, oneDto);
            oneDto.setTitle(StrUtil.isNotBlank(oneActivity.getTitleCn()) ? oneActivity.getTitleCn() : oneActivity.getTitleEn());
            oneDto.setDescription(StrUtil.isNotBlank(oneActivity.getDescriptionCn()) ? oneActivity.getDescriptionCn() : oneActivity.getDescriptionEn());
            oneDto.setActivityConfig(null);
            oneDto.setJoinRule(null);
            oneDto.setAwardRule(null);

            activityDtoList.add(oneDto);
        }

        page.setElements(activityDtoList);
        resultObject.setData(page);
        return resultObject;
    }

    /**
     * 保存活动
     *
     * @param request
     * @param activityInfo
     * @return
     */
    @PostMapping("/saveActivity.action")
    public Object saveActivity(HttpServletRequest request, @RequestBody ActivityEditInfoDTO activityInfo) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        if (StrUtil.isBlank(activityInfo.getTemplateId())) {
            resultObject.setCode("-1");
            resultObject.setMsg("未指定活动类型");
            return resultObject;
        }

        // 操作账号
        String operator = request.getParameter("username");
        activityInfo.setCreateBy(operator);

        ActivityTemplate templateEntity = activityTemplateService.getById(activityInfo.getTemplateId());
        if (templateEntity == null || templateEntity.getStatus() != 1) {
            resultObject.setCode("-1");
            resultObject.setMsg("该类型的活动已下线");
            return resultObject;
        }
        activityInfo.setType(templateEntity.getType());

        ActivityLibrary activityLibrary = null;
        if (StrUtil.isNotBlank(activityInfo.getId()) && !Objects.equals(activityInfo.getId(), "0")) {
            activityLibrary = activityLibraryService.findById(activityInfo.getId());
            // 判断是否允许修改活动
            try {
                checkUpdateActivity(activityLibrary, activityInfo);
            } catch (BusinessException e) {
                resultObject.setCode("-1");
                resultObject.setMsg(e.getMessage());
                return resultObject;
            }
        }
//        else {
//            ActivityLibrary checkExistCode = activityLibraryService.getByCode(activityInfo.getActivityCode());
//            if (checkExistCode != null) {
//                resultObject.setCode("-1");
//                resultObject.setMsg("活动编码冲突");
//                return resultObject;
//            }
//            activityInfo.setActivityCode(activityInfo.getActivityCode());
//        }

        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(templateEntity.getType());
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);

        try {
            // 将下面一段代码放到数据库事务中执行
            FunParams inputParam = FunParams.newParam()
                    .set("activityInfo", activityInfo);
            FunParams result = transactionMethodFragmentFun.runInTransaction(inputParam, param -> {
                ActivityEditInfoDTO activityInfoParam = param.get("activityInfo").getAs(ActivityEditInfoDTO.class);

                ActivityLibrary activityLibraryParam = handler.saveActivity(activityInfoParam);

                // 同时备份历史，经测试好像如果此处的 saveLog 报错，不影响数据提交
                List<ActivityPrize> curPrizeList = activityPrizeService.listByActivityId(activityLibraryParam.getId().toString(), 1);
                activityConfigLogService.saveLog(activityLibraryParam, curPrizeList);

                param.set("activityLibrary", activityLibraryParam);
                return param;
            });
            activityLibrary = result.get("activityLibrary").getAs(ActivityLibrary.class);
        } catch (BusinessException e) {
            resultObject.setCode("-1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }

        // TODO
        activityInfo = handler.getActivityDetail(activityLibrary.getId().toString(), null);

        resultObject.setData(activityInfo);
        return resultObject;
    }

    /**
     * 给用户加减积分
     *
     * @param partyId
     * @return
     */
    @GetMapping("/getActivityPoint.action")
    public Object getActivityPoint(@RequestParam String partyId) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        ActivityTypeEnum typeEnum = ActivityTypeEnum.SIMPLE_LOTTERY;
        ActivityLibrary activityLibrary = activityLibraryService.findByType(typeEnum.getType());
        if (StrUtil.isBlank(partyId) || activityLibrary == null) {
            resultObject.setCode("-1");
            resultObject.setMsg("参数不正确");
            return resultObject;
        }

        // 基于活动 id 给用户加积分
        ActivityUserPoints activityUserPoints = activityUserPointsService.saveOrGetUserPoints(typeEnum.getType(), "0", partyId);

        Map<String, Object> retData = new HashMap<>();
        retData.put("points", activityUserPoints.getPoints());

        resultObject.setMsg("succeed");
        resultObject.setData(retData);
        return resultObject;
    }

//    /**
//     * 给用户加减积分
//     *
//     * @param partyId
//     * @return
//     */
//    @PostMapping("/addActivityPoint.action")
//    public Object addActivityPoint(@RequestParam String partyId,
//                                   @RequestParam int accPoint,
//                                   @RequestParam int accType) {
//        ResultObject resultObject = new ResultObject();
//        resultObject.setMsg("success");
//
//        ActivityTypeEnum typeEnum = ActivityTypeEnum.SIMPLE_LOTTERY;
//        ActivityLibrary activityLibrary = activityLibraryService.findByType(typeEnum.getType());
//        if (StrUtil.isBlank(partyId) || activityLibrary == null) {
//            resultObject.setCode("-1");
//            resultObject.setMsg("参数不正确");
//            return resultObject;
//        }
//
//        // 基于活动 id 给用户加积分
//        ActivityUserPoints activityUserPoints = activityUserPointsService.saveOrGetUserPoints("0", partyId);
//        int reducePoints = accPoint;
//        if (accType <= 0) {
//            reducePoints = -accPoint;
//        }
//        activityUserPointsService.updatePoints(activityUserPoints.getId().toString(), reducePoints);
//
//        activityUserPoints = activityUserPointsService.getById(activityUserPoints.getId().toString());
//        Map<String, Object> retData = new HashMap<>();
//        retData.put("points", activityUserPoints.getPoints());
//
//        resultObject.setMsg("succeed");
//        resultObject.setData(retData);
//        return resultObject;
//    }

    @DeleteMapping("/delete.action")
    public Object delete(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String id = request.getParameter("id");
        // 操作账号
        String operator = request.getParameter("username");

        ActivityLibrary activityLibrary = activityLibraryService.findById(id);
        if (activityLibrary == null) {
            return resultObject;
        }
        if (activityLibrary.getStatus() != 0) {
            resultObject.setMsg("-1");
            resultObject.setMsg("活动已启用，不允许删除");
            return resultObject;
        }

        activityPrizeService.deleteActivityPrizeLogic(id);
        activityLibraryService.deleteLogic(id);

        return resultObject;
    }

    @GetMapping("/detail.action")
    public Object activityEditInfo(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        resultObject.setMsg("success");

        String activityId = request.getParameter("activityId");

        ActivityLibrary activityLibrary = activityLibraryService.findById(activityId);
        if (activityLibrary == null) {
            throw new BusinessException("活动不存在");
        }

        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityLibrary.getType());
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);

        ActivityEditInfoDTO activityInfo = handler.getActivityDetail(activityId, null);

        resultObject.setData(activityInfo);
        return resultObject;
    }

    private void checkUpdateActivity(ActivityLibrary oldActivityEntity, ActivityEditInfoDTO activityInfo) {
        BigDecimal totalOdds = BigDecimal.ZERO;
        for (ActivityPrizeDTO onePrizeDto : activityInfo.getPrizeList()) {
            BigDecimal curOdds = onePrizeDto.getOdds();//new BigDecimal(onePrizeDto.getOdds());
            if (curOdds.doubleValue() < 0) {
                throw new BusinessException("中奖几率不能小于 0");
            }
            totalOdds = totalOdds.add(curOdds);
            if (totalOdds.floatValue() > 1.0) {
                throw new BusinessException("累计中奖几率不能大于 1");
            }
        }
        if (totalOdds.floatValue() < 1.0) {
            throw new BusinessException("累计中奖几率之和必须为 1");
        }

        int oriStatus = oldActivityEntity.getStatus();
        int newStatus = activityInfo.getStatus();

        if (oriStatus == 2) {
            throw new BusinessException("活动已结束，无需修改，你可以重新创建活动");
        }
        if (oriStatus == 0) {
            // 活动未开始，可以随便改，包括修改活动类型
            return;
        }

        Date now = new Date();
        // 强制改成未启动，此时不管原始活动是已启动还是未启动，都允许修改启动状态、时间、奖品、奖励规则等等
        if (newStatus == 0) {
            return;
        }

        // 活动正在进行时，不建议随便修改配置
        if (oriStatus == 1) {
            // 活动原来处于启用状态，需要限制修改的范围
            if (oldActivityEntity.getStartTime().after(now)) {
                // 活动还没正式开始，允许随便修改
                return;
            }

            if (!Objects.equals(oldActivityEntity.getType(), activityInfo.getType())) {
                throw new BusinessException("活动运行期间不允许修改活动类型");
            }

            // 活动有效运行期间不允许增删奖品
            // 提取有效奖品记录
            List<ActivityPrize> oriPrizeList = activityPrizeService.listByActivityId(oldActivityEntity.getId().toString(), 1);
            Map<String, ActivityPrize> oriPrizeMap = oriPrizeList.stream()
                    .collect(Collectors.toMap(entity -> entity.getPoolId(), Function.identity(), (key1, key2) -> key2));

            List<ActivityPrizeDTO> newPrizeList = activityInfo.getPrizeList();

            for (ActivityPrizeDTO oneNewPrize : newPrizeList) {
                if (!oriPrizeMap.containsKey(oneNewPrize.getPoolId())) {
                    throw new BusinessException("活动运行期间不允许增减奖品");
                }
                oriPrizeMap.remove(oneNewPrize.getPoolId());
            }
            if (!oriPrizeMap.isEmpty()) {
                throw new BusinessException("活动运行期间不允许增减奖品");
            }

            // 活动配置校验 TODO
        }
    }
}
