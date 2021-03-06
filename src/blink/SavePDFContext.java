package blink;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class SavePDFContext extends AbstractAction {

	/** for serialisation */
	private static final long serialVersionUID = 979401257782838856L;

	private JPanel _panel;
	private VisualizationViewer _view;

	public SavePDFContext(JPanel panel, VisualizationViewer view) {
		super("PDF");
		_panel = panel;
		_view = view;
	}
	
	public SavePDFContext(JPanel panel, VisualizationViewer view, String label) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(_view == null) {
			OptionPanel.getInstance().setPrintPanel(_panel);
		} else {
			OptionPanel.getInstance().setPrintPanel(_view);
		}
		OptionPanel.getInstance().setVisible(true);
//		hardwork();
	}

//	public void hardwork() {
//		// java.awt.Rectangle dim = _gemViewer.getBounds();
//		// int width = dim.width;
//		// int height = dim.height;
//		// Rectangle dim =
//		// ((MyTutteLayout)_gemViewer.getLayout()).boundingBox();
//		int width = 600;
//		int height = 800;
//		JFileChooser fc = new JFileChooser();
//		fc.setMultiSelectionEnabled(true);
//		String lastPath = App.getProperty("lastSavePDFContext");
//		if (lastPath != null) {
//			fc.setSelectedFile(new File(lastPath));
//		}
//		int r = fc.showSaveDialog(_panel);
//		if (r == JFileChooser.APPROVE_OPTION) {
//			File selFile = fc.getSelectedFile();
//			App.setProperty("lastSavePDFContext", selFile.getAbsolutePath());
//			// print the panel to pdf
//			Document document = new Document();
//			try {
//				PdfWriter writer = PdfWriter.getInstance(document,
//						new FileOutputStream(selFile));
//				document.open();
//				PdfContentByte contentByte = writer.getDirectContent();
//				PdfTemplate template = contentByte.createTemplate(500, 660);
////				Graphics2D g2 = template.createGraphics(500, 500);
//				Graphics2D g2 = new PdfGraphics2D(contentByte, width, height);
//				// the idea is that "width" and "height" might change their values
//				// for now a fixed value is being used
//				double scx = (500 / (double) width);
//				double scy = (500 / (double) height);
//				g2.scale(scx, scx);
//				if(_view == null) {
//					_panel.print(g2);
//				} else {
//					Renderer normal = _view.getRenderer();
//					_view.setRenderer(PanelGemViewer.bwRender);
//					_view.print(g2);
//					_view.setRenderer(normal);
//				}
//				g2.dispose();
//				contentByte.addTemplate(template, 30, 300);
//				// contentByte.addTemplate(template,
//				// AffineTransform.getScaleInstance(template.getWidth()/dim.width,
//				// template.getHeight()/dim.height));
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (document.isOpen()) {
//					document.close();
//				}
//			}
//		}
//	}
	
	static class OptionPanel extends JFrame implements ActionListener {
		/** for serialisation */
		private static final long serialVersionUID = 151159040686682956L;
		
		private String okStr = "OK";
		private String coloredStr = "Colored";
		private String nonColoredStr = "Black and white";
		private static JButton okButton;
		
		private static OptionPanel op;
		
		private JRadioButton colored;
		private JRadioButton nonColored;
		private JPanel printPanel;
		private JTextField savePathField;
		private JTextField imgLabelField;
		private JFileChooser fc;
		private File printFile;
		
		public static OptionPanel getInstance() {
			if(op == null) {
				op = new OptionPanel();
			}
			return op;
		}
		
		private OptionPanel() {
			this.setPreferredSize(new Dimension(240, 200));
			this.setSize(280, 150);
			JPanel panel = new JPanel(new GridBagLayout());
			fc = new JFileChooser();
			
			savePathField = new JTextField("Select...");
			savePathField.setEditable(false);
			savePathField.setPreferredSize(new Dimension(230, 20));
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 2;
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(savePathField, gbc);
			
			JButton fcButton = new JButton(new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String lastPath = App.getProperty("lastSavePDFContext");
					if (lastPath != null) {
						fc.setSelectedFile(new File(lastPath));
					}
					int r = fc.showSaveDialog(printPanel);
					if (r == JFileChooser.APPROVE_OPTION) {
						printFile = fc.getSelectedFile();
						savePathField.setText(printFile.getAbsolutePath());
						String[] splitPath = printFile.getAbsolutePath().split("/");
						imgLabelField.setText(splitPath[splitPath.length-1]);
					}
				}
			});
			fcButton.setLabel("...");
			fcButton.setPreferredSize(new Dimension(18, 20));
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			panel.add(fcButton, gbc);
			
			JLabel imgLabelLabel = new JLabel("Label:");
			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = 1;
			panel.add(imgLabelLabel, gbc);
			
			imgLabelField = new JTextField("");
			imgLabelField.setPreferredSize(new Dimension(180, 20));
			gbc = new GridBagConstraints();
			gbc.gridwidth = 2;
			gbc.gridx = 1;
			gbc.gridy = 1;
			panel.add(imgLabelField, gbc);
			
			colored = new JRadioButton(coloredStr);
			colored.setActionCommand(coloredStr);
			nonColored = new JRadioButton(nonColoredStr);
			nonColored.setActionCommand(nonColoredStr);
			nonColored.setSelected(true);
			ButtonGroup bgroup = new ButtonGroup();
			bgroup.add(colored);
			bgroup.add(nonColored);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			panel.add(colored, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			panel.add(nonColored, gbc);
			
			okButton = new JButton(okStr);
			okButton.setPreferredSize(new Dimension(60, 30));
			okButton.setActionCommand(okStr);
			okButton.addActionListener(this);
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			panel.add(okButton, gbc);
			
			JButton cancelButton = new JButton(new AbstractAction() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					op.dispatchEvent(new WindowEvent(op, WindowEvent.WINDOW_CLOSING));
				}
			});
			cancelButton.setPreferredSize(new Dimension(90, 30));
			cancelButton.setLabel("Cancel");
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 3;
			panel.add(cancelButton, gbc);
						
			this.setContentPane(panel);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setResizable(false);
			this.setTitle("PDF export");
		}
		
		public void setPrintPanel(JPanel printPanel) {
			this.printPanel = printPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals(okStr)) {
				int width = 600;
				int height = 800;
				
				if(printFile != null) {
					App.setProperty("lastSavePDFContext", printFile.getAbsolutePath());
					// print the panel to pdf
					Document document = new Document();
					try {
						PdfWriter writer = PdfWriter.getInstance(document,
								new FileOutputStream(printFile));
						document.open();
						PdfContentByte contentByte = writer.getDirectContent();
						PdfTemplate template = contentByte.createTemplate(500, 660);
//						Graphics2D g2 = template.createGraphics(500, 500);
						Graphics2D g2 = new PdfGraphics2D(contentByte, width, height);
						// the idea is that "width" and "height" might change their values
						// for now a fixed value is being used
						double scx = (500 / (double) width);
						double scy = (500 / (double) height);
						g2.scale(scx, scx);
						
//						label.setPreferredSize(new Dimension(200, 70));
						if(imgLabelField.getText() != null) {
							LayoutManager normalLayout = printPanel.getLayout();
							printPanel.setLayout(null);
							JLabel label = new JLabel(imgLabelField.getText());
							printPanel.add(label);
							Insets insets = printPanel.getInsets();
							label.setFont(new java.awt.Font("Serif", Font.BOLD, 14));
							Dimension size = label.getPreferredSize();
							label.setBounds(10+insets.left, 10+insets.top, size.width, size.height);
							if(nonColored.isSelected() && printPanel instanceof VisualizationViewer) {
								VisualizationViewer view = (VisualizationViewer) printPanel;
								Renderer normal = view.getRenderer();
								view.setRenderer(PanelGemViewer.bwRender);
								view.print(g2);
								view.setRenderer(normal);
							} else {
								printPanel.print(g2);
							}
							printPanel.remove(label);
							printPanel.setLayout(normalLayout);
							g2.dispose();
							contentByte.addTemplate(template, 30, 300);
							// contentByte.addTemplate(template,
							// AffineTransform.getScaleInstance(template.getWidth()/dim.width,
							// template.getHeight()/dim.height));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (document.isOpen()) {
							document.close();
						}
					}
					this.dispose();
				}
			}
		}
		
	}

}
