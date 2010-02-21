package org.protege.editor.core.ui.list;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Feb-2007<br>
 * <br>
 */
public class MList extends JList {
	/**
     * 
     */
    private static final long serialVersionUID = -7803414142701944703L;
    private MListCellRenderer ren;
	private MListDeleteButton deleteButton;
	private MListEditButton editButton;
	private MListAddButton addButton;
	private static final Stroke BUTTON_STROKE = new BasicStroke(2.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private boolean mouseDown;
	private static final int BUTTON_DIMENSION = 16;
	private static final int BUTTON_MARGIN = 2;
	private static Font SECTION_HEADER_FONT = new Font("Lucida Grande",
			Font.PLAIN, 10);
	private static final Color itemBackgroundColor = new Color(240, 245, 240);
	private List<MListButton> editAndDeleteButtons;
	private List<MListButton> deleteButtonOnly;
	private ActionListener deleteActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MList.this.handleDelete();
		}
	};
	private ActionListener addActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MList.this.handleAdd();
		}
	};
	private ActionListener editActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MList.this.handleEdit();
		}
	};
	private MouseMotionListener mouseMovementListener = new MouseMotionAdapter() {
		public int lastIndex = 0;

		@Override
		public void mouseMoved(MouseEvent e) {
			if (MList.this.getModel().getSize() > 0) {
				Point pt = MList.this.getMousePosition();
				// more efficient than repainting the whole component every time
				// the mouse moves
				if (pt != null) {
					int index = MList.this.locationToIndex(pt);
					// only repaint all the cells the mouse has moved over
					MList.this.repaint(MList.this.getCellBounds(Math.min(index,
							this.lastIndex), Math.max(index, this.lastIndex)));
					this.lastIndex = index;
				} else {
					MList.this.repaint();
					this.lastIndex = 0;
				}
			}
		}
	};
	private MouseListener mouseButtonListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			MList.this.mouseDown = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			MList.this.handleMouseClick(e);
			MList.this.mouseDown = false;
		}

		@Override
		public void mouseExited(MouseEvent event) {
			// leave the component cleanly
			MList.this.repaint();
		}
	};

	public MList() {
		ListCellRenderer renderer = this.getCellRenderer();
		this.ren = new MListCellRenderer();
		this.ren.setContentRenderer(renderer);
		super.setCellRenderer(this.ren);
		this.deleteButton = new MListDeleteButton(this.deleteActionListener) {
			@Override
			public String getName() {
				String name = "<html><body>" + super.getName();
				String rowName = MList.this.getRowName(this.getRowObject());
				if (rowName != null) {
					name += " " + rowName.toLowerCase();
				}
				return name + "</body></html>";
			}
		};
		this.addButton = new MListAddButton(this.addActionListener);
		this.editButton = new MListEditButton(this.editActionListener);
		this.addMouseMotionListener(this.mouseMovementListener);
		this.addMouseListener(this.mouseButtonListener);
		this.setFixedCellHeight(-1);
		this.deleteButtonOnly = new ArrayList<MListButton>();
		this.deleteButtonOnly.add(this.deleteButton);
		this.editAndDeleteButtons = new ArrayList<MListButton>();
		this.editAndDeleteButtons.add(this.editButton);
		this.editAndDeleteButtons.add(this.deleteButton);
	}

	protected String getRowName(Object rowObject) {
		return null;
	}

	@Override
	public void setCellRenderer(ListCellRenderer cellRenderer) {
		if (this.ren == null) {
			super.setCellRenderer(cellRenderer);
		} else {
			this.ren.setContentRenderer(cellRenderer);
		}
	}

	protected void handleAdd() {
		if (this.getSelectedValue() instanceof MListItem) {
			MListItem item = (MListItem) this.getSelectedValue();
			item.handleEdit();
		}
	}

	protected void handleDelete() {
		if (this.getSelectedValue() instanceof MListItem) {
			MListItem item = (MListItem) this.getSelectedValue();
			item.handleDelete();
		}
	}

	protected void handleEdit() {
		if (this.getSelectedValue() instanceof MListItem) {
			MListItem item = (MListItem) this.getSelectedValue();
			item.handleEdit();
		}
	}

	private void handleMouseClick(MouseEvent e) {
		for (MListButton button : this.getButtons(this.locationToIndex(e
				.getPoint()))) {
			if (button.getBounds().contains(e.getPoint())) {
				button.getActionListener().actionPerformed(
						new ActionEvent(button, ActionEvent.ACTION_PERFORMED,
								button.getName()));
				return;
			}
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	protected List<MListButton> getButtons(Object value) {
		if (value instanceof MListSectionHeader) {
			return this.getSectionButtons((MListSectionHeader) value);
		} else if (value instanceof MListItem) {
			return this.getListItemButtons((MListItem) value);
		} else {
			return Collections.emptyList();
		}
	}

	protected List<MListButton> getSectionButtons(MListSectionHeader header) {
		List<MListButton> buttons = new ArrayList<MListButton>();
		if (header.canAdd()) {
			buttons.add(this.addButton);
		}
		return buttons;
	}

	protected List<MListButton> getListItemButtons(MListItem item) {
		if (item.isDeleteable()) {
			if (item.isEditable()) {
				return this.editAndDeleteButtons;
			} else {
				return this.deleteButtonOnly;
			}
		}
		return Collections.emptyList();
	}

	protected Color getItemBackgroundColor(MListItem item) {
		return itemBackgroundColor;
	}

	public class MListCellRenderer implements ListCellRenderer {
		private ListCellRenderer contentRenderer;
		private DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			// We now modify the component so that it has a nice border and
			// background
			if (value instanceof MListSectionHeader) {
				JLabel label = (JLabel) this.defaultListCellRenderer
						.getListCellRendererComponent(list, " ", index,
								isSelected, cellHasFocus);
				label.setBorder(BorderFactory.createCompoundBorder(MList.this
						.createPaddingBorder(list, " ", index, isSelected,
								cellHasFocus), BorderFactory.createEmptyBorder(
						2, 2, 2, 2)));
				return label;
			}
			JComponent component = (JComponent) this.contentRenderer
					.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
			component.setOpaque(true);
			if (value instanceof MListItem) {
				Border border = BorderFactory.createCompoundBorder(MList.this
						.createPaddingBorder(list, value, index, isSelected,
								cellHasFocus), MList.this.createListItemBorder(
						list, value, index, isSelected, cellHasFocus));
				int buttonSpan = MList.this.getButtons(value).size()
						* (BUTTON_DIMENSION + 2) + BUTTON_MARGIN * 2;
				border = BorderFactory.createCompoundBorder(border,
						BorderFactory.createEmptyBorder(1, 1, 1, buttonSpan));
				component.setBorder(border);
				if (!isSelected) {
					component.setBackground(MList.this
							.getItemBackgroundColor((MListItem) value));
				}
			}
			if (isSelected) {
				component.setBackground(list.getSelectionBackground());
			}
			return component;
		}

		public void setContentRenderer(ListCellRenderer renderer) {
			this.contentRenderer = renderer;
		}
	}

	protected Border createPaddingBorder(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		int bottomMargin = 1;
		if (list.getFixedCellHeight() == -1) {
			if (this.getModel().getSize() > index + 1) {
				if (this.getModel().getElementAt(index + 1) instanceof MListSectionHeader) {
					bottomMargin = 20;
				}
			}
		}
		return BorderFactory.createMatteBorder(1, 1, bottomMargin, 1,
				Color.WHITE);
	}

	protected Border createListItemBorder(JList list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		return BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY);
	}

	private List<MListButton> getButtons(int index) {
		if (index < 0) {
			return Collections.emptyList();
		}
		Object obj = this.getModel().getElementAt(index);
		List<MListButton> buttons = this.getButtons(obj);
		Rectangle rowBounds = this.getCellBounds(index, index);
		if (obj instanceof MListSectionHeader) {
			MListSectionHeader section = (MListSectionHeader) obj;
			Rectangle nameBounds = this.getGraphics().getFontMetrics(
					SECTION_HEADER_FONT).getStringBounds(section.getName(),
					this.getGraphics()).getBounds();
			int x = 7 + nameBounds.width + 2;
			for (MListButton button : buttons) {
				button.setBounds(new Rectangle(x, rowBounds.y + 2,
						BUTTON_DIMENSION, BUTTON_DIMENSION));
				x += BUTTON_DIMENSION;
				x += 2;
				button.setRowObject(obj);
			}
		} else if (obj instanceof MListItem) {
			int x = rowBounds.width - 2;
			for (MListButton button : buttons) {
				x -= BUTTON_DIMENSION;
				x -= 2;
				button.setBounds(new Rectangle(x, rowBounds.y + 2,
						BUTTON_DIMENSION, BUTTON_DIMENSION));
				button.setRowObject(obj);
			}
		}
		return buttons;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point mousePos = this.getMousePosition();
		if (mousePos == null) {
			return null;
		}
		for (MListButton button : this.getButtons(this
				.locationToIndex(mousePos))) {
			if (button.getBounds().contains(mousePos)) {
				return button.getName();
			}
		}
		int index = this.locationToIndex(event.getPoint());
		if (index == -1) {
			return null;
		}
		Object val = this.getModel().getElementAt(index);
		if (val instanceof MListItem) {
			return ((MListItem) val).getTooltip();
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color oldColor = g.getColor();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// Paint buttons
		Stroke oldStroke = g2.getStroke();
		Rectangle clipBound = g.getClipBounds();
		boolean paintedSomeRows = false;
		boolean useQuartz = Boolean.getBoolean(System
				.getProperty("-Dapple.awt.graphics.UseQuartz"));
		for (int index = 0; index < this.getModel().getSize(); index++) {
			Rectangle rowBounds = this.getCellBounds(index, index);
			if (!rowBounds.intersects(clipBound)) {
				if (paintedSomeRows) {
					break;
				}
				continue;
			}
			paintedSomeRows = true;
			List<MListButton> buttons = this.getButtons(index);
			int endOfButtonRun = -1;
			for (MListButton button : buttons) {
				Rectangle buttonBounds = button.getBounds();
				if (buttonBounds.intersects(clipBound)) {
					g2.setColor(this.getButtonColor(button));
					if (!useQuartz) {
						g2
								.fillOval(buttonBounds.x, buttonBounds.y,
										buttonBounds.width + 1,
										buttonBounds.height + 1);
					} else {
						g2.fillOval(buttonBounds.x, buttonBounds.y,
								buttonBounds.width, buttonBounds.height);
					}
					g2.setColor(Color.WHITE);
					Stroke curStroke = g2.getStroke();
					g2.setStroke(BUTTON_STROKE);
					button.paintButtonContent(g2);
					g2.setStroke(curStroke);
					// g2.translate(-buttonBounds.x, -buttonBounds.y);
				}
				endOfButtonRun = buttonBounds.x + buttonBounds.width
						+ BUTTON_MARGIN;
			}
			if (this.getModel().getElementAt(index) instanceof MListSectionHeader) {
				MListSectionHeader header = (MListSectionHeader) this
						.getModel().getElementAt(index);
				if (this.isSelectedIndex(index)) {
					g2.setColor(this.getSelectionForeground());
				} else {
					g2.setColor(Color.GRAY);
				}
				int indent = 4;
				int baseLine = rowBounds.y
						+ (BUTTON_DIMENSION + BUTTON_MARGIN - g
								.getFontMetrics().getHeight()) / 2
						+ g.getFontMetrics().getAscent();
				Font oldFont = g2.getFont();
				g2.setFont(SECTION_HEADER_FONT);
				g2.drawString(header.getName(), 1 + indent, baseLine);
				g2.setFont(oldFont);
				if (endOfButtonRun == -1) {
					endOfButtonRun = g2.getFontMetrics(SECTION_HEADER_FONT)
							.getStringBounds(header.getName(), g2).getBounds().width
							+ BUTTON_MARGIN * 2;
				}
				int midLine = rowBounds.y + (BUTTON_DIMENSION + BUTTON_MARGIN)
						/ 2;
				// g2.drawLine(endOfButtonRun, midLine, getWidth() - 2, midLine);
			}
		}
		g.setColor(oldColor);
		g2.setStroke(oldStroke);
	}

	private Color getButtonColor(MListButton button) {
		Point pt = this.getMousePosition();
		if (pt == null) {
			return button.getBackground();
		}
		if (button.getBounds().contains(pt)) {
			if (this.mouseDown) {
				return Color.DARK_GRAY;
			} else {
				return button.getRollOverColor();
			}
		}
		return button.getBackground();
	}

	public static class TestItem implements MListItem {
		private String s;

		public TestItem(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return this.s;
		}

		public boolean isEditable() {
			return true;
		}

		public void handleEdit() {
		}

		public boolean isDeleteable() {
			return true;
		}

		public boolean handleDelete() {
			return false;
		}

		public String getTooltip() {
			return "tooltip";
		}
	}

	public static class TestHeader implements MListSectionHeader {
		public String getName() {
			return "This is a test section";
		}

		public boolean canAdd() {
			return true;
		}

		public void handleAdd() {
		}
	}

	public static void main(String[] args) {
		MList list = new MList();
		JFrame frame = new JFrame();
		frame.setContentPane(list);
		frame.setVisible(true);
		list.setListData(new Object[] { new TestHeader(), new TestItem("A"),
				new TestItem("B"), new TestHeader(), new TestItem("C"),
				"NOT MLI" });
	}
}
