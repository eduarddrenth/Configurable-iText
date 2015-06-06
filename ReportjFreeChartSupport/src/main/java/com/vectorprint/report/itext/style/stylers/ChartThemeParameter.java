/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint.report.itext.style.stylers;

/*
 * #%L
 * ReportjFreeChartSupport
 * %%
 * Copyright (C) 2013 - 2014 VectorPrint
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.vectorprint.configuration.annotation.Setting;
import com.vectorprint.configuration.annotation.SettingsAnnotationProcessor;
import com.vectorprint.configuration.annotation.SettingsField;
import com.vectorprint.configuration.parameters.ParameterImpl;
import com.vectorprint.report.jfree.ChartThemeBuilder;

/**
 * Gives you the possibility to configure a {@link ChartThemeBuilder} using settings. {@link Chart} will call
 * {@link SettingsAnnotationProcessor#initSettings(java.lang.Object, com.vectorprint.configuration.EnhancedMap) }
 * the instantiated {@link ChartThemeBuilder}, so you can use {@link Setting} and {@link SettingsField} in your own implementation
 * of {@link ChartThemeBuilder}.
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ChartThemeParameter extends ParameterImpl<ChartThemeBuilder>{

   public ChartThemeParameter(String key, String help) {
      super(key, help,ChartThemeBuilder.class);
   }
   

}
