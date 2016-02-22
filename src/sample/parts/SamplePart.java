/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package sample.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import sample.handlers.openpart;

public class SamplePart {

	private final class AbstractParameterValueConverterExtension extends AbstractParameterValueConverter {
		@Override
		public String convertToString(Object parameterValue) throws ParameterValueConversionException {

			return parameterValue.toString();
		}

		@Override
		public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
			return new String(parameterValue);
		}
	}

	private Text txtInput;
	private TableViewer tableViewer;
	@Inject ECommandService  commandService;
	@Inject EHandlerService handlerService;
	private String S_CMD_MY_COMMAND_ID = "sample.command.openpart";
	@Inject
	private MDirtyable dirty;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dirty.setDirty(true);
			}
		});
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(parent);

		tableViewer.add("Sample item 1");
		tableViewer.add("Sample item 2");
		tableViewer.add("Sample item 3");
		tableViewer.add("Sample item 4");
		tableViewer.add("Sample item 5");
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object firstElement = selection.getFirstElement();
				Map<String, Object> parameters = new HashMap<String, Object>();
				final Command command = commandService.getCommand(S_CMD_MY_COMMAND_ID);
				ParameterType parameterType;
				try {
					parameterType = command.getParameterType("PartName");
					if (parameterType != null) {          
						parameterType.define(String.class.getCanonicalName(), new AbstractParameterValueConverter() {
							
							@Override
							public String convertToString(Object parameterValue) throws ParameterValueConversionException {
								return parameterValue.toString();
							}
							
							@Override
							public Object convertToObject(String parameterValue) throws ParameterValueConversionException {
								return new String(parameterValue);
							}
						});
					}
					parameters.put("PartName", firstElement);
					ParameterizedCommand myCommand = commandService.createCommand(S_CMD_MY_COMMAND_ID, parameters);
					handlerService.activateHandler(S_CMD_MY_COMMAND_ID, new openpart());
					if (!handlerService.canExecute(myCommand))
						return;
					handlerService.executeHandler(myCommand);// this will execute your handler as mentioned in part 2
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}



			}
		});
	}

	@Focus
	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}