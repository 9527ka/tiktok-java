package project.invest.walletday;

import kernel.web.Page;

public interface WalletDayService {

     Page pagedQuery(int pageNo, int pageSize);

     void updateWalletDay(double amount);

}