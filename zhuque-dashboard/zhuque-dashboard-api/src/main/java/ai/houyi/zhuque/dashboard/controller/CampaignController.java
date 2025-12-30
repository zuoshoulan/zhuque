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
package ai.houyi.zhuque.dashboard.controller;

import java.util.Arrays;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ai.houyi.zhuque.commons.page.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.commons.web.IController;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.model.AuthContext;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.model.query.CampaignQueryReq;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.core.service.CampaignService;
import org.springframework.web.bind.annotation.RequestMapping;
import ai.houyi.zhuque.dao.model.Campaign;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author weiping wang
 */
@RestController
@RequestMapping("/campaigns")
@Api(tags="推广活动/计划管理")
public class CampaignController implements IController<Campaign, CampaignQueryReq, Integer> {
	@Autowired
	private CampaignService campaignService;

	@PostMapping
	@RequestMapping
	public void saveOrUpdate(Campaign t) {
		t.setAdvertiserId(AuthContext.currentUser().getId());
		if (t.getId() == null) {
			campaignService.save(t);
		} else {
			campaignService.update(t);
		}
	}

	@DeleteMapping
	@RequestMapping("/{id:[0-9]+}")
	public void deleteById(Integer id) {
		campaignService.softDeleteById(id);
	}

	@GetMapping
	@RequestMapping("/{id:[0-9]+}")
	public Campaign loadById(Integer id) {
		return campaignService.loadById(id);
	}

	@PostMapping
	@RequestMapping("/list")
	public Page<Campaign> selectPage(CampaignQueryReq queryReq) {
		queryReq.setAdvertiserIds(Arrays.asList(AuthContext.currentUser().getId()));
		return campaignService.selectPageList(queryReq);
	}
}
