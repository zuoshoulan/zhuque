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
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.houyi.zhuque.auth.service.PermissionService;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.commons.page.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.commons.web.IController;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.model.query.PermissionQueryReq;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.dao.model.Permission;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author weiping wang
 */
@RestController
@RequestMapping("/permissions")
@Api(tags="权限管理")
public class PermissionController implements IController<Permission, PermissionQueryReq, Integer> {
	@Autowired
	private PermissionService permissionService;
	
	@PostMapping
	public void saveOrUpdate(Permission t) {
		if(t.getId()==null) {
			permissionService.save(t);
		}else {
			permissionService.update(t);
		}
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable Integer id) {
		permissionService.deleteById(id);
	}

	@GetMapping("/{id}")
	public Permission loadById(@PathVariable Integer id) {
		return permissionService.loadById(id);
	}

	@GetMapping
	public List<Permission> permissionTree(){
		return permissionService.selectPermissionsAsTree();
	}
	
	@Override
	public Page<Permission> selectPage(PermissionQueryReq queryReq) {
		//DO NOTHING
		return null;
	}
}
