/*
 * Copyright (c) 2001-2017, Zoltan Farkas All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Additionally licensed with:
 *
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
 */
package org.spf4j.configDiscovery.maven.plugin;

import javax.annotation.Nullable;
import org.spf4j.base.asm.UnknownValue;

/**
 *
 * @author zoly
 */
public final class FieldInfo<T> {

  private final String namespace;

  private final Class<T> type;

  private final Object defaultValue;

  private final String doc;

  public FieldInfo(final String namespace, final String doc, final Class<T> type, final T defaultValue) {
    this.namespace = namespace;
    this.type = type;
    if (defaultValue != null && defaultValue.getClass() == UnknownValue.class) {
      this.defaultValue = null;
    } else {
      this.defaultValue = defaultValue;
    }
    this.doc = doc;
  }

  public Class<?> getType() {
    return type;
  }

  @Nullable
  public Object getDefaultValue() {
    return defaultValue;
  }

  public String getDoc() {
    return doc;
  }

  public String getNamespace() {
    return namespace;
  }


  @Override
  public String toString() {
    return "FieldInfo{" + "type=" + type + ", defaultValue=" + defaultValue + ", doc=" + doc + '}';
  }


}
