/*
 * Copyright 2017-2019 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.zhuque.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ai.houyi.zhuque.auth.model.ChangePwdReq;
import ai.houyi.zhuque.auth.model.ResetPasswdReq;
import ai.houyi.zhuque.auth.service.UserService;
import ai.houyi.zhuque.commons.page.Page;
import ai.houyi.zhuque.commons.web.IController;
import ai.houyi.zhuque.core.model.query.UserQueryReq;
import ai.houyi.zhuque.dao.model.Permission;
import ai.houyi.zhuque.dao.model.Role;
import ai.houyi.zhuque.dao.model.User;
import io.swagger.annotations.Api;

/**
 * @author weiping wang
 */
@RestController
@RequestMapping("/users")
@Api(tags = "用户管理")
public class UserController implements IController<User, UserQueryReq, Integer> {

	@Autowired
	private UserService userService;

	@PostMapping
	public void saveOrUpdate(User user) {
		if (user.getId() == null) {
			userService.save(user);
		} else {
			userService.update(user);
		}
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable Integer id) {
		userService.deleteById(id);
	}

	@GetMapping("/{id}")
	public User loadById(@PathVariable Integer id) {
		return userService.loadById(id);
	}

	@PostMapping("/list")
	public Page<User> selectPage(UserQueryReq queryReq) {
		queryReq.initPageInfoIfNeed();
		return userService.selectPageList(queryReq);
	}

	// 管理员强制更新用户密码
	@PostMapping("/passwd/reset")
	public void resetPasswd(@RequestBody ResetPasswdReq req) {
		userService.resetPasswd(req);
	}

	@PostMapping("/passwd/update")
	public void changePasswd(@RequestBody ChangePwdReq req) {
		userService.updatePasswd(req);
	}

	@PostMapping("/roles/{userId}")
	public void setRoles(@PathVariable Integer userId, @RequestBody List<Integer> roleIds) {
		userService.updateUserRoles(userId, roleIds);
	}

	@GetMapping("/roles/{userId}")
	public List<Role> getUserRoles(@PathVariable Integer userId) {
		return userService.getUserRoles(userId);
	}
	
	@GetMapping("/permissions/{userId}")
	public List<Permission> getUserPermissions(@PathVariable Integer userId){
		return userService.getUserPermissions(userId);
	}
}
