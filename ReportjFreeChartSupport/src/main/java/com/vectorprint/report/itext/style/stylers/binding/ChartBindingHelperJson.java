package com.vectorprint.report.itext.style.stylers.binding;

/*
 * #%L
 * ReportjFreeChartSupport
 * %%
 * Copyright (C) 2014 - 2015 VectorPrint
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
import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.report.itext.style.parameters.binding.JsonReportBindingHelper;
import com.vectorprint.report.jfree.ChartThemeBuilder;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ChartBindingHelperJson extends JsonReportBindingHelper {


   @Override
   public <T> T convert(String value, Class<T> clazz) {
      if (ChartThemeBuilder.class.equals(clazz)) {
         try {
            return (T) Class.forName(value).newInstance();
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new VectorPrintRuntimeException(ex);
         }
      } else {
         return super.convert(value, clazz);
      }
   }

}
