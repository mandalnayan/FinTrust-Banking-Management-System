package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.annotation.Command;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class UserProfileVM {

    private User user;
    private boolean editMode = false;
    private UserService userService;

    @Init
    public void init() {
        // Load user data (e.g., from database or session)
    	userService = new UserServiceImpl();
        user = userService.getLoggedInUser();
        Messagebox.show("Hii: " + user);
    }

    @Command
    @NotifyChange("editMode")
    public void toggleEditMode() {
        editMode = !editMode;
    }

    @Command
    @NotifyChange("user")
    public void updateProfile() {
    	userService.updateUser(user);
        editMode = false;
        Clients.showNotification("Profile updated successfully!", "info", null, "top_center", 3000);
    }

    @Command
    public void changePassword() {
        // Logic for password change dialog
        Executions.createComponents("/views/change-password.zul", null, null);
    }

    @Command
    @NotifyChange("user")
    public void toggle2FA() {
        user.setTwoFactor(!user.isTwoFactor());
        userService.update2FA(user);
        Clients.showNotification("Two-Factor Authentication setting updated.", "info", null, "top_center", 3000);
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
