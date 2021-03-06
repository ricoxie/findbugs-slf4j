package jp.skypencil.findbugs.slf4j;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

public class UsingNonConstantAsFormatTest {
  @Test
  public void test() {
    Map<String, Integer> expected = Maps.newHashMap();
    expected.put("SLF4J_FORMAT_SHOULD_BE_CONST", 2);
    expected.put("DM_CONVERT_CASE", 1);
    new XmlParser().expect(pkg.UsingNonConstantAsFormat.class, expected);
  }
}
