/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

public class SearchFilter {

	public enum Operator {
		EQ, LIKE, GT, LT, GTE, LTE,IN,ISNULL,NOTNULL,NEQ
	}

	public String fieldName;
	public Object value;
	public Operator operator;

	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}

	public static Map<String, SearchFilter> filters() {
		return Maps.newIdentityHashMap();
	}

	/**
	 * searchParams中key的格式为OPERATOR_FIELDNAME
	 */
	public static Map<String, SearchFilter> parse(Map<String, Object> searchParams) {
		//LTE and GTE 实现between
		Map<String, SearchFilter> filters = Maps.newIdentityHashMap();

		for (Entry<String, Object> entry : searchParams.entrySet()) {
			// 过滤掉空值
			String key = entry.getKey();
			Object value = entry.getValue();
			if (StringUtils.isBlank((String) value)||"NULL".equals(value)) {
				continue;
			}

			//IN_name_String
			//GT_date_DATETIME
			//ISNULL_type
			//NEQ_status

			// 拆分operator、filedAttribute、type
			String[] names = StringUtils.split(key, "_");
//			if (names.length < 2) {
//				throw new IllegalArgumentException(key + " is not a valid search filter name");
//			}
			String filedName = names[1];
			Operator operator = Operator.valueOf(names[0]);
			String type=names.length==3?names[2]:null;//TODO Long Integer
			switch (operator) {
				case IN:
					String v = value.toString();
					String[] strings = v.split(",");
					value= Arrays.asList(strings);//TODO type
					break;
			}

			if("DATETIME".equals(type)){
				try {
					value=DateUtils.parseDate(value.toString(),"yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					e.printStackTrace();//TODO
				}
			}else if("DATE".equals(type)){
				try {
					value=DateUtils.parseDate(value.toString(),"yyyy-MM-dd");
				} catch (ParseException e) {
					e.printStackTrace();//TODO
				}
			}else if("TIME".equals(type)){
				try {
					value=DateUtils.parseDate(value.toString(),"HH:mm:ss");
				} catch (ParseException e) {
					e.printStackTrace();//TODO
				}
			}

			// 创建searchFilter
			SearchFilter filter = new SearchFilter(filedName, operator, value);
			filters.put(new String(key), filter);
		}

		return filters;
	}
}
