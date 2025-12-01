package com.fintrust.viewModel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.annotation.Command;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.Account;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

public class UserProfileVM {

	private UserDetailsServiceImpl userService;
	private AccountService accountService;
	private UserDetails userDetails;
	private Account selectedAccount;
    private boolean editMode = false;
    
    private List<Account> accountList;

    @Init
    @NotifyChange("userDetails")
    public void init() {
    		accountService = new AccountServiceImpl();
       	accountList = accountService.getAllAccounts();
       	if (accountList == null || accountList.size() == 0) {
			NotificationUtil.showInstant("error", "Faild to load account details. Please refresh the page");
			
		} else {
			selectedAccount = accountList.get(0);
		}
        userService = new UserDetailsServiceImpl();
        userDetails = userService.getLogedInDetails();       
        Sessions.getCurrent().setAttribute("user", userDetails);
    }
    
	@NotifyChange("selectedAccount")
	public void setSelectedAccount(Account selectedAccount) {
		if (selectedAccount != null) { 
			this.selectedAccount = selectedAccount;
			userService.updatePrimaryAccount(1, selectedAccount.getAccountId());
		}
	}
	    
    @Command
    @NotifyChange("editMode")
    public void toggleEditMode() {
        editMode = !editMode;
    }

    @Command
    @NotifyChange("userDetails")
    public void updateProfile() {
    	if(userService.updateProfile(userDetails)) {
        editMode = false;
        NotificationUtil.showInstant("info", "User Details updated successfully");
    	} else {
 		NotificationUtil.showInstant("error", "Failed to update. Please try again");
    	}
    }

    @Command
    public void changePassword() {
        // Logic for password change dialog
        Clients.showNotification("Invoked", "info", null, "top_center", 3000);

        Executions.createComponents("/change-password.zul", null, null);
        Executions.sendRedirect("/change-password.zul");
    }

    @Command
    @NotifyChange("userDetails")
    public void toggle2FA() {
//    	userDetails.setTwoFactor(!userDetails.getTwoFactor());
//        userService.update2FA(userDetails);
//        Clients.showNotification("Two-Factor Authentication setting updated.", "info", null, "top_center", 3000);
//   
    }

    // Getters and setters
    public UserDetails getUserDetails() {
        return userDetails;
    }
    
 // Return accountlist
 	public List<Account> getAccountList() {
 		return accountList;
 	}
 	
 	// Get Selected Account
 	public Account getSelectedAccount() {
		return selectedAccount;
	}

    public boolean isEditMode() {
        return editMode;
    }
    
    /**
     * Getting registered date in specific formate(YYY-MM-dd)
     * @return
     */
    public String getRegisteredDateFormatted() {
        if (userDetails == null
                || userDetails.getUser() == null
                || userDetails.getUser().getCreatedAt() == null) {
            return "";
        }

        LocalDateTime ldt = userDetails.getUser().getCreatedAt();

        return ldt.toLocalDate()
                  .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
    
    /**
     * Getting DOB date in specific formate(dd-MM-YYY)
     * @return
     */
    public Date getDob() {
        if (userDetails == null
                || userDetails.getDob() == null) {
            return new Date();
        }


        LocalDate localDate = userDetails.getDob();

        Date date = Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        return date;
    }

}
