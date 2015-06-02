/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint.report.itext.style.stylers;

import com.vectorprint.report.data.DataCollectorImpl;
import com.vectorprint.report.itext.style.parameters.ReportBindingHelper;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public abstract class JfreeDataCollector extends DataCollectorImpl {

   @Override
   public Class<? extends ReportBindingHelper> getDefaultBindingHelperClass() {
      return ChartBindingHelper.class;
   }
   
   

}
