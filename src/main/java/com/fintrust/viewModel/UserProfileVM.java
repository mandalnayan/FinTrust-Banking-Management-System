package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.annotation.Command;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.Customer;
import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class UserProfileVM {

    private Customer customer;
    private boolean editMode = false;
    private UserService userService;

    @Init
    @NotifyChange("customer")
    public void init() {
        userService = new UserServiceImpl();
        customer = userService.getLoggedInUser();
        
        Sessions.getCurrent().setAttribute("customer", customer);
    }
    
    @Command
    @NotifyChange("editMode")
    public void toggleEditMode() {
        editMode = !editMode;
    }

    @Command
    @NotifyChange("customer")
    public void updateProfile() {
    	if(userService.updateUser(customer)) {
        editMode = false;
        Clients.showNotification("Profile updated successfully!", "info", null, "top_center", 3000);
    	} else {
//    		user = (User) Sessions.getCurrent().getAttribute("user");
    		 Clients.showNotification("Failed to updated. Please try again", "error", null, "top_center", 3000);
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
    @NotifyChange("user")
    public void toggle2FA() {
    	customer.setTwoFactor(!customer.getTwoFactor());
        userService.update2FA(customer);
        Clients.showNotification("Two-Factor Authentication setting updated.", "info", null, "top_center", 3000);
    }

    // Getters and setters
    public Customer getCustomer() {
        return customer;
    }

    public boolean isEditMode() {
        return editMode;
    }
    
    /**
     * Getting registered date in specific formate(YYY-MM-dd)
     * @return
     */
    public String getRegisteredDateFormatted() {
        if (customer == null || customer.getRegisteredDate() == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd")
                .format(customer.getRegisteredDate());
    }
}
