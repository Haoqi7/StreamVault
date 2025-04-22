package com.flower.spirit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.flower.spirit.utils.BiliUtil;

@SpringBootTest
class SpiritApplicationTests {

	@Test
	void contextLoads() {
		BiliUtil.ArcSearch("319521269", null);
	}

}
