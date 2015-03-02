package com.vectorprint.report.jfree;

/*
 * #%L
 * VectorPrintReport4.0
 * %%
 * Copyright (C) 2012 - 2013 VectorPrint
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
//~--- non-JDK imports --------------------------------------------------------
import com.vectorprint.configuration.EnhancedMap;
import com.vectorprint.report.itext.style.stylers.Chart;
import java.io.Serializable;
import org.jfree.chart.ChartTheme;

//~--- JDK imports ------------------------------------------------------------
/**
 * Responsible for instantiating a {@link ChartTheme} from {@link EnhancedMap settings}
 *
 * @see Chart#THEMEBUILDER
 * @see ChartBuilder#ChartBuilder(com.vectorprint.report.jfree.ChartBuilder.CHARTTYPE, org.jfree.data.general.Dataset,
 * java.lang.String, java.lang.String, java.lang.String, boolean, boolean,
 * com.vectorprint.report.jfree.ChartThemeBuilder, com.vectorprint.configuration.EnhancedMap)
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public interface ChartThemeBuilder extends Serializable {

   ChartTheme fromSettings(EnhancedMap settings);

}
