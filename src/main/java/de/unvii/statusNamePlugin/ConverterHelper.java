package de.unvii.statusNamePlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConverterHelper {

    public static final String SEPARATOR = ";";

    public static List<String> convertMapToSeparatedList(Map<UUID, String> map) {
        return map.entrySet().stream() //
                .map(entry -> entry.getKey() + SEPARATOR + entry.getValue()) //
                .toList();
    }

    public static Map<UUID, String> convertSeparatedListToMap(List<String> separatedList) {
        return separatedList.stream() //
                .map(kv -> kv.split(SEPARATOR)) //
                .filter(kvArray -> kvArray.length == 2) //
                .collect(Collectors.toMap(kv -> UUID.fromString(kv[0]), kv -> kv[1]));
    }
}
