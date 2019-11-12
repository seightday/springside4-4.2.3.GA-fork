/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.persistence;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springside.modules.utils.Collections3;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicSpecifications {

	public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters, final Class<T> entityClazz) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				if (Collections3.isNotEmpty(filters)) {

					List<Predicate> predicates = Lists.newArrayList();
					for (SearchFilter filter : filters) {
						// nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
						String[] names = StringUtils.split(filter.fieldName, ".");
						Path expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}

						// logic operator
						switch (filter.operator) {
							case EQ:
								predicates.add(builder.equal(expression, filter.value));
								break;
							case LIKE:
								predicates.add(builder.like(expression, "%" + filter.value + "%"));
								break;
							case GT:
								predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
								break;
							case LT:
								predicates.add(builder.lessThan(expression, (Comparable) filter.value));
								break;
							case GTE:
								predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
								break;
							case LTE:
								predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
								break;
							case IN:
								predicates.add(expression.in((Collection<?>)filter.value));
								break;
							case NOTIN:
								predicates.add(expression.in((Collection<?>)filter.value).not());
								break;
							case ISNULL:
								predicates.add(builder.isNull(expression));
								break;
							case NOTNULL:
								predicates.add(builder.isNotNull(expression));
								break;
							case NEQ:
								predicates.add(builder.notEqual(expression,filter.value));
								break;
						}
					}


					// 将所有条件用 and 联合起来
					if (!predicates.isEmpty()) {
						return builder.and(predicates.toArray(new Predicate[predicates.size()]));
					}
				}

				return builder.conjunction();
			}
		};
	}
	public static <T> Specification<T> bySearchFilter(Root root, CriteriaBuilder builder,final Collection<SearchFilter> filters,Predicate... ps) {

		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> _root, CriteriaQuery<?> _query, CriteriaBuilder _builder) {
				if (Collections3.isNotEmpty(filters)) {

					List<Predicate> predicates = Lists.newArrayList();
					for (SearchFilter filter : filters) {
						// nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
						String[] names = StringUtils.split(filter.fieldName, ".");
						Path expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}

						// logic operator
						switch (filter.operator) {
							case EQ:
								predicates.add(builder.equal(expression, filter.value));
								break;
							case LIKE:
								predicates.add(builder.like(expression, "%" + filter.value + "%"));
								break;
							case GT:
								predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
								break;
							case LT:
								predicates.add(builder.lessThan(expression, (Comparable) filter.value));
								break;
							case GTE:
								predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
								break;
							case LTE:
								predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
								break;
							case IN:
								predicates.add(expression.in((Collection<?>)filter.value));
								break;
							case ISNULL:
								predicates.add(builder.isNull(expression));
								break;
							case NEQ:
								predicates.add(builder.notEqual(expression,filter.value));
								break;
						}
					}

					if(ArrayUtils.isNotEmpty(ps)){
						List<Predicate> list = Arrays.asList(ps);
						List<Predicate> collect = list.stream().filter(e -> {
							return e != null;
						}).collect(Collectors.toList());
						predicates.addAll(collect);
					}

					// 将所有条件用 and 联合起来
					if (!predicates.isEmpty()) {
						return builder.and(predicates.toArray(new Predicate[predicates.size()]));
					}
				}

				return builder.conjunction();
			}
		};
	}

	public static <T> Specification<T> idSpec(final Object id) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				return builder.equal(root.get("id"), id);
			}
		};
	}
}
