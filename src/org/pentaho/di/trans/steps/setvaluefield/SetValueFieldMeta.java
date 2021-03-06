/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.setvaluefield;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;



public class SetValueFieldMeta extends BaseStepMeta implements StepMetaInterface
{
	private static Class<?> PKG = SetValueFieldMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

	private String fieldName[];
	private String replaceByFieldValue[];
	
	public SetValueFieldMeta()
	{
		super(); // allocate BaseStepMeta
	}
	
	/**
     * @return Returns the fieldName.
     */
    public String[] getFieldName()
    {
        return fieldName;
    }
    
    /**
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String[] fieldName)
    {
        this.fieldName = fieldName;
    }
 
    /**
     * @return Returns the replaceByFieldValue.
     */
    public String[] getReplaceByFieldValue()
    {
        return replaceByFieldValue;
    }
    
    /**
     * @param replaceByFieldValue The replaceByFieldValue to set.
     */
    public void setReplaceByFieldValue(String[] replaceByFieldValue)
    {
        this.replaceByFieldValue = replaceByFieldValue;
    }
 	
	
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException
    {
        readData(stepnode, databases);
    }

	public void allocate(int count)
	{
		fieldName  = new String[count];
		replaceByFieldValue = new String[count];
	}

	public Object clone()
	{
		SetValueFieldMeta retval = (SetValueFieldMeta)super.clone();

		int count=fieldName.length;
		
		retval.allocate(count);
				
		for (int i=0;i<count;i++)
		{
			retval.fieldName[i]  = fieldName[i];
			retval.replaceByFieldValue[i] = replaceByFieldValue[i];
		}
		
		return retval;
	}
	
	   private void readData(Node stepnode, List<? extends SharedObjectInterface> databases) throws KettleXMLException
	   {
		try
		{
			Node fields = XMLHandler.getSubNode(stepnode, "fields"); //$NON-NLS-1$
			int count= XMLHandler.countNodes(fields, "field"); //$NON-NLS-1$
			
			allocate(count);
					
			for (int i=0;i<count;i++)
			{
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i); //$NON-NLS-1$
				
				fieldName[i]  = XMLHandler.getTagValue(fnode, "name"); //$NON-NLS-1$
				replaceByFieldValue[i] = XMLHandler.getTagValue(fnode, "replaceby"); //$NON-NLS-1$
			}
		}
		catch(Exception e)
		{
			throw new KettleXMLException(BaseMessages.getString(PKG, "SetValueFieldMeta.Exception.UnableToReadStepInfoFromXML"), e); //$NON-NLS-1$
		}
	}
	
	public void setDefault()
	{
		int count=0;
		
		allocate(count);

		for (int i=0;i<count;i++)
		{
			fieldName[i] = "field"+i; //$NON-NLS-1$
			replaceByFieldValue[i] = ""; //$NON-NLS-1$
		}
	}

	public String getXML()
	{
        StringBuffer retval = new StringBuffer();

		retval.append("    <fields>"+Const.CR); //$NON-NLS-1$
		
		for (int i=0;i<fieldName.length;i++)
		{
			retval.append("      <field>"+Const.CR); //$NON-NLS-1$
			retval.append("        "+XMLHandler.addTagValue("name", fieldName[i])); //$NON-NLS-1$ //$NON-NLS-2$
			retval.append("        "+XMLHandler.addTagValue("replaceby", replaceByFieldValue[i])); //$NON-NLS-1$ //$NON-NLS-2$
			retval.append("        </field>"+Const.CR); //$NON-NLS-1$
		}
		retval.append("      </fields>"+Const.CR); //$NON-NLS-1$

		return retval.toString();
	}
	
	 public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException
	  {
		try
		{
			int nrfields = rep.countNrStepAttributes(id_step, "field_name"); //$NON-NLS-1$
			
			allocate(nrfields);
	
			for (int i=0;i<nrfields;i++)
			{
				fieldName[i] =          rep.getStepAttributeString(id_step, i, "field_name"); //$NON-NLS-1$
				replaceByFieldValue[i] = 		rep.getStepAttributeString(id_step, i, "replace_by"); //$NON-NLS-1$
			}
		}
		catch(Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "SetValueFieldMeta.Exception.UnexpectedErrorReadingStepInfoFromRepository"), e); //$NON-NLS-1$
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
		throws KettleException
	{
		try
		{
			for (int i=0;i<fieldName.length;i++)
			{
				rep.saveStepAttribute(id_transformation, id_step, i, "field_name",      fieldName[i]); //$NON-NLS-1$
				rep.saveStepAttribute(id_transformation, id_step, i, "replace_by",     replaceByFieldValue[i]); //$NON-NLS-1$
			}
		}
		catch(Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "SetValueFieldMeta.Exception.UnableToSaveStepInfoToRepository")+id_step, e); //$NON-NLS-1$
		}

	}

	
	 public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepinfo, RowMetaInterface prev, String input[], String output[], RowMetaInterface info)
	 {	
		 CheckResult cr;
		if (prev==null || prev.size()==0)
			cr = new CheckResult(CheckResult.TYPE_RESULT_WARNING, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.NoReceivingFieldsError"), stepinfo); //$NON-NLS-1$
		else
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.StepReceivingFieldsOK",prev.size()+""), stepinfo); //$NON-NLS-1$ //$NON-NLS-2$
		remarks.add(cr);
		
		
		// See if we have input streams leading to this step!
		if (input.length>0)
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.StepRecevingInfoFromOtherSteps"), stepinfo); //$NON-NLS-1$
		else
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.NoInputReceivedError"), stepinfo); //$NON-NLS-1$
		remarks.add(cr);
		
		if(fieldName==null && fieldName.length==0)
		{
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.FieldsSelectionEmpty"), stepinfo); //$NON-NLS-1$
			remarks.add(cr);	
		}else
		{
			for(int i=0;i<fieldName.length;i++)
			{
				if(Const.isEmpty(replaceByFieldValue[i]))
				{
					cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "SetValueFieldMeta.CheckResult.ReplaceByValueMissing",fieldName[i],""+i), stepinfo); //$NON-NLS-1$
					remarks.add(cr);
				}
			}
		}
	}


	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans trans)
	{
		return new SetValueField(stepMeta, stepDataInterface, cnr, transMeta, trans);
	}

	public StepDataInterface getStepData()
	{
		return new SetValueFieldData();
	}
    public boolean supportsErrorHandling()
    {
        return true;
    }
}
