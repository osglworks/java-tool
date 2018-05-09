package org.osgl.issues.g77;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;

import java.util.List;
import java.util.Map;

public class Gh77 extends TestBase {

    @Test
    public void test() {
        Map<String, String> courseData = C.Map("name", "Math");
        List<?> courseList = C.list(courseData);
        Map<String, Object> userData = C.Map("name", "Tom", "courses", courseList);
        User user = new User();
        user = $.map(userData).to(user);
        List<?> userCourses = user.courses;
        yes(userCourses.get(0) instanceof Course);
    }

}
