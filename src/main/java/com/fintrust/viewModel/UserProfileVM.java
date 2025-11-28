package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.annotation.Command;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class UserProfileVM {

	private UserDetailsServiceImpl userService;
    private boolean editMode = false;
    private UserDetails userDetails;

    @Init
    @NotifyChange("customer")
    public void init() {
        userService = new UserDetailsServiceImpl();
        userDetails = userService.getLogedInDetails();
        
        Sessions.getCurrent().setAttribute("user", userDetails);
    }
    
    @Command
    @NotifyChange("editMode")
    public void toggleEditMode() {
        editMode = !editMode;
    }

    @Command
    @NotifyChange("userDetails")
    public void updateProfile() {
    	if(userService.updateDetails(userDetails)) {
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
    @NotifyChange("userDetails")
    public void toggle2FA() {
    	userDetails.setTwoFactor(!userDetails.getTwoFactor());
        userService.update2FA(userDetails);
        Clients.showNotification("Two-Factor Authentication setting updated.", "info", null, "top_center", 3000);
    }

    // Getters and setters
    public UserDetails getuserDetails() {
        return userDetails;
    }

    public boolean isEditMode() {
        return editMode;
    }
    
    /**
     * Getting registered date in specific formate(YYY-MM-dd)
     * @return
     */
    public String getRegisteredDateFormatted() {
        if (userDetails == null || userDetails.getCreatedAt() == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd")
                .format(userDetails);
    }
}
