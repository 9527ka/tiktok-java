package project.web.admin.report;

import kernel.web.PageActionSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.invest.walletday.WalletDayService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 用户存量资金汇总
 *
 */
@RestController
@RequestMapping("/brush/userMoney")
public class AdminMonitorUserMoneyStatisticsController extends PageActionSupport {

    @Resource
    protected WalletService walletService;

    @Resource
    protected WalletDayService walletDayService;

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        List<Wallet> wallets = walletService.findAllWallet();
        double amount = wallets.stream().mapToDouble(Wallet::getMoney).sum();
        String amounts = new BigDecimal(amount).setScale(8, RoundingMode.FLOOR).toPlainString();
        ModelAndView model = new ModelAndView();
        model.addObject("amount", amounts);
        model.setViewName("auto_monitor_statistics_user_money_list");
        return model;
    }

    /**
     * 详情
     * @param request
     * @return
     */
    @RequestMapping("/walletDayList.action")
    public ModelAndView walletDayList(HttpServletRequest request) {
        this.pageSize = 20;
        this.checkAndSetPageNo(request.getParameter("pageNo"));
        this.page = walletDayService.pagedQuery(pageNo, pageSize);
        ModelAndView model = new ModelAndView();
        model.addObject("page", page);
        model.addObject("pageNo", pageNo);
        model.setViewName("admin_wallet_day_list");
        return model;
    }
}