package ru.keich.mon.indexedhashmap;

import java.util.Map;

public record Metrics(
		Long objectsSize,
		Long added,
		Long updated,
		Long removed,
		Map<String ,Long> indexSize
		) { }
