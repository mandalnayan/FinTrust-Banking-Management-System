package com.fintrust.admin.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.util.NotificationUtil;

public class userPopupDetails extends SelectorComposer<Component>{
	 private static final long serialVersionUID = -5378213970005720355L;
	 @Wire private Label userNameLabel, userEmailLabel, userGenderLabel, userDOBLabel, userDistrictLabel, userStateLabel, userCountryLabel;
	 @Wire Button closeBtn;
	 
	 private Long selectedUser;
	 
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
        selectedUser =  (Long) Executions.getCurrent().getSession().getAttribute("selected_user_id");
        if (selectedUser == null) return;
        
        if (selectedUser == null) {
        	NotificationUtil.showInstant("warning", "Request not found!");
            Executions.sendRedirect("index.zul");
            return;
        }
        
        UserDetailsServiceImpl UserDetailsServiceImpl = new UserDetailsServiceImpl();
        UserDetails userDetails = UserDetailsServiceImpl.getUserDetails(selectedUser);
        System.out.println(userDetails);
          
        userNameLabel.setValue(userDetails.getUser().getFullName() +"");
        userEmailLabel.setValue(userDetails.getUser().getEmail() +"");
        userGenderLabel.setValue(userDetails.getGender() +"");
        userDOBLabel.setValue(userDetails.getDob() +"");
        userDistrictLabel.setValue(userDetails.getDistrict() +"");
        userStateLabel.setValue(userDetails.getState() +"");
        userCountryLabel.setValue(userDetails.getCountry()+"");
	}
	
	@Listen("onClick = #closeBtn")
    public void onBackClick() {
      	Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/admin/customerDetails.zul");
    }
}
