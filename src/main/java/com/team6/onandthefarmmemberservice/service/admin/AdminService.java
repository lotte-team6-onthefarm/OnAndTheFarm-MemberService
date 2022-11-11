package com.team6.onandthefarmmemberservice.service.admin;

import com.team6.onandthefarmmemberservice.dto.admin.AdminReIssueDto;
import com.team6.onandthefarmmemberservice.vo.admin.AdminLoginResponse;

import javax.servlet.http.HttpServletRequest;

public interface AdminService {
    AdminLoginResponse reIssueToken(AdminReIssueDto adminReIssueDto);
    Boolean logout(HttpServletRequest request);
}
