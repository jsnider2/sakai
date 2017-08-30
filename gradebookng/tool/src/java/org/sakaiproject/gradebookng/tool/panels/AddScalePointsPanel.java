package org.sakaiproject.gradebookng.tool.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.gradebookng.business.GbGradingType;
import org.sakaiproject.gradebookng.business.GbRole;
import org.sakaiproject.gradebookng.business.GradebookNgBusinessService;
import org.sakaiproject.gradebookng.business.model.GbGroup;
import org.sakaiproject.gradebookng.tool.component.GbAjaxButton;
import org.sakaiproject.gradebookng.tool.component.GbFeedbackPanel;
import org.sakaiproject.gradebookng.tool.model.GradebookUiSettings;
import org.sakaiproject.gradebookng.tool.pages.GradebookPage;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.Model;

import lombok.Getter;
import lombok.Setter;
import org.sakaiproject.service.gradebook.shared.GraderPermission;
import org.sakaiproject.service.gradebook.shared.PermissionDefinition;

/**
 *
 * Panel for the modal window that allows an instructor to add points to all grades for an assignment
 *
 */
public class AddScalePointsPanel extends Panel {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "org.sakaiproject.gradebookng.business.GradebookNgBusinessService")
    protected GradebookNgBusinessService businessService;
    private final IModel<Long> model;
    private final ModalWindow window;
    private static final double DEFAULT_VALUE = 0;

    public AddScalePointsPanel(final String id, final IModel<Long> model, final ModalWindow window) {
        super(id);
        this.model = model;
        this.window = window;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        final Long assignmentId = this.model.getObject();
        final Assignment assignment = this.businessService.getAssignment(assignmentId.longValue());

        final GbGradingType gradeType = GbGradingType.valueOf(this.businessService.getGradebook().getGrade_type());

        final GradeScale gradeScale = new GradeScale();
        gradeScale.setPoints(String.valueOf(DEFAULT_VALUE));
        gradeScale.setAssignmentId(assignmentId);
        final CompoundPropertyModel<GradeScale> formModel = new CompoundPropertyModel<GradeScale>(gradeScale);

        final Form<GradeScale> form = new Form<GradeScale>("form", formModel);

        final GbAjaxButton submit = new GbAjaxButton("submit") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(final AjaxRequestTarget target, final Form<?> form) {

                final GradeScale gs = (GradeScale) form.getModelObject();
                final boolean success = AddScalePointsPanel.this.businessService.addScalePoints(assignment.getId(), assignment.getPoints(), Double.parseDouble(gs.getPoints()));

                if (success) {
                    AddScalePointsPanel.this.window.close(target);
                    setResponsePage(GradebookPage.class);
                }

            }
        };
        form.add(submit);

        final GbAjaxButton cancel = new GbAjaxButton("cancel") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                AddScalePointsPanel.this.window.close(target);
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        Model<Double> pointsTextFieldModel = Model.of(0.0);
        final NumberTextField<Double> pointsTextField = new NumberTextField<Double>("points", pointsTextFieldModel, Double.class);        
        pointsTextField.setStep(0.1);
        if (gradeType == GbGradingType.PERCENTAGE) 
        {
            pointsTextField.setMinimum(-50.0);
            pointsTextField.setMaximum(50.0);
            form.add(pointsTextField.setRequired(true));
            form.add(new Label("pointsSuffix", "%"));
        } 
        else 
        {
            Double halfPoints = assignment.getPoints() / 2;
            pointsTextField.setMinimum(halfPoints * -1.0);
            pointsTextField.setMaximum(halfPoints);
            form.add(pointsTextField.setRequired(true));
            form.add(new Label("pointsSuffix", "points"));
        }

        add(form);

        form.add(new GbFeedbackPanel("addScalePointsFeedback"));
    }

    /**
     * Model for this form
     */
    class GradeScale implements Serializable {

        private static final long serialVersionUID = 1L;

        @Getter
        @Setter
        private String points;

        @Getter
        @Setter
        private Long assignmentId;
    }
}
