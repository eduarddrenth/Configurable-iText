/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vectorprint.report.itext.style.stylers;

import com.vectorprint.VectorPrintRuntimeException;
import com.vectorprint.report.itext.style.parameters.ReportBindingHelper;
import com.vectorprint.report.jfree.ChartThemeBuilder;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ChartBindingHelper extends ReportBindingHelper {


   @Override
   public <T> T convert(String value, Class<T> clazz) {
      if (ChartThemeBuilder.class.equals(clazz)) {
         try {
            return (T) Class.forName(value).newInstance();
         } catch (ClassNotFoundException ex) {
            throw new VectorPrintRuntimeException(ex);
         } catch (InstantiationException ex) {
            throw new VectorPrintRuntimeException(ex);
         } catch (IllegalAccessException ex) {
            throw new VectorPrintRuntimeException(ex);
         }
      } else {
         return super.convert(value, clazz);
      }
   }

}
