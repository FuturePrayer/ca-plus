package cn.suhoan.caplus.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.suhoan.caplus.dto.LoginVo;
import cn.suhoan.caplus.dto.ResultVo;
import cn.suhoan.caplus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FuturePrayer
 * @date 2025/4/22
 */
@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultVo<Void> login(@RequestBody LoginVo loginVo) {
        boolean login = userService.login(loginVo.username(), loginVo.password());
        if (login) {
            StpUtil.login(loginVo.username());
            return ResultVo.ok();
        } else {
            return ResultVo.error("用户名或密码错误");
        }
    }
}
