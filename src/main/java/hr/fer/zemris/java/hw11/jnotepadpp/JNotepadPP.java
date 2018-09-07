package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.interfaces.MultipleDocumentListener;
import hr.fer.zemris.java.hw11.interfaces.SingleDocumentModel;
import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LJMenu;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LJToolBar;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizableAction;
import hr.fer.zemris.java.hw11.jnotepadpp.local.LocalizationProvider;

/**
 * A custom Notepad program, inspired by Notepad++. Offers basic features such 
 * as loading, saving and opening files(in multiple tabs). Also has additional 
 * features such as sorting selected text, filtering duplicate lines, and printing
 * statistical data about the file, upon request.
 * 
 * Has full localization support, where the currently supported languages are 
 * English, German, and Croatian. 
 * @author 0036502252
 *
 */
public class JNotepadPP extends JFrame {
	/**
	 * Auto-generated UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default size of the window.
	 */
	private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	/**
	 * Default location of the window.
	 */
	private static final Point DEFAULT_LOCATION = new Point(50, 50);
	/**
	 * Default title of the window.
	 */
	private static final String TITLE = "JNotepad++";

	/**
	 * Localization tag for the Croatian language.
	 */
	protected static final String HRVATSKI = "hr";
	/**
	 * Localization tag for the German language.
	 */
	protected static final String DEUTSCH = "de";
	/**
	 * Localization tag for the English language.
	 */
	protected static final String ENGLISH = "en";

	/**
	 * Indicates whether or not the status bar is initialized.
	 */
	private static boolean statusBarInitialized = false;

	/**
	 * The document model used in this program.
	 */
	private DefaultMultipleDocumentModel documents;
	/**
	 * The text area for each tab.
	 */
	private JTextArea editor;
	/**
	 * The label in the status bar indicating total document length.
	 */
	private JLabel lengthLabel;
	/**
	 * The label in the status bar indicating total caret info.
	 */
	private JLabel caretInfoLabel;
	/**
	 * The label in the status bar which serves as a clock.
	 */
	private JLabel clock;
	/**
	 * The status bar.
	 */
	private JPanel statusBar;
	/**
	 * The tools menu popup window.
	 */
	private JMenu toolsMenu;
	/**
	 * The edit menu popup window.
	 */
	private JMenu editMenu;
	/**
	 * The sort menu popup window.
	 */
	private JMenu sortMenu;

	/**
	 * The {@link FormLocalizationProvider} used in this program for
	 * localization implementation.
	 */
	private FormLocalizationProvider flp = new FormLocalizationProvider(
			LocalizationProvider.getInstance(), this);

	/**
	 * Constructs a new {@link JNotepadPP} frame.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(DEFAULT_SIZE);
		setLocation(DEFAULT_LOCATION);
		setTitle(TITLE);

		initGUI();

		setVisible(true);
	}

	/**
	 * Initializes the graphical user interface.
	 */
	private void initGUI() {
		Container cp = getContentPane();
		documents = new DefaultMultipleDocumentModel(flp);

		cp.setLayout(new BorderLayout());
		cp.add(documents, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				boolean closingAborted = checkForUnsavedDocuments();
				if (!closingAborted) {
					dispose();
					System.exit(0);
				}
			}
		});

		documents.addChangeListener(l -> {
			Path p = documents.getCurrentDocument().getFilePath();
			
			this.setTitle(
					(p == null ? 
					flp.getString("blank") :
					p.toString()) + " - " + TITLE
			);

			editor = documents.getCurrentDocument().getTextComponent();
			updateBar();
		});

		addMenus();
		addToolbar();
		initActions();
		addStatusBar();

	}

	/**
	 * Utility method. Clears the status bar.
	 */
	private void clearBar() {
		lengthLabel.setText("");
		caretInfoLabel.setText("");
	}

	/**
	 * Initializes the status bar.
	 */
	private void initStatusBar() {
		statusBar = new JPanel(new GridLayout(1, 3));
		statusBar.setBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		statusBar.setPreferredSize(new Dimension(getWidth(), 16));

		lengthLabel.setHorizontalAlignment(SwingConstants.LEFT);

		caretInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);

		statusBar.add(lengthLabel);
		statusBar.add(caretInfoLabel);
		statusBar.add(clock);

		getContentPane().add(statusBar, BorderLayout.SOUTH);
	}

	/**
	 * Adds the status bar to the program's frame.
	 */
	private void addStatusBar() {
		lengthLabel = new JLabel();
		caretInfoLabel = new JLabel();

		addClock();

		if (!statusBarInitialized) {
			initStatusBar();
			statusBarInitialized = true;
		}

		documents.addMultipleDocumentListener(new MultipleDocumentListener() {
			@Override
			public void documentRemoved(SingleDocumentModel model) {
				if (documents.getNumberOfDocuments() == 0)
					clearBar();
			}

			@Override
			public void documentAdded(SingleDocumentModel model) {
				// do nothing
			}

			@Override
			public void currentDocumentChanged(
					SingleDocumentModel previousModel,
					SingleDocumentModel currentModel) {

				if (editor != null && editor.getCaretListeners().length == 0) {
					editor.addCaretListener(e -> {
						toggleSelectionActions();
						updateBar();
					});
				}
			}

		});
	}

	/**
	 * Toggles the menus and actions which in some way or another modify the
	 * selected part of the document. If no selection is made, these
	 * menus/actions are simply disabled.
	 */
	private void toggleSelectionActions() {
		toolsMenu.setEnabled(editor.getSelectedText() != null);
		sortMenu.setEnabled(editor.getSelectedText() != null);
		uniqueAction.setEnabled(editor.getSelectedText() != null);
	}

	/**
	 * Adds a clock to the bottom status bar.
	 */
	private void addClock() {
		clock = new JLabel();
		Timer timer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clock.setText(
						LocalDateTime.now().format(
							DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
						)
				);
			}
		});

		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.setInitialDelay(0);
		timer.start();

	}

	/**
	 * Updates the bottom status bar with new information.
	 */
	private void updateBar() {
		lengthLabel.setText(flp.getString("length") + editor.getText().length());

		int row = 0;
		int col = 0;
		
		try {
			row = editor.getLineOfOffset(editor.getCaretPosition());
			col = editor.getCaretPosition() - editor.getLineStartOffset(row);
		} catch (BadLocationException ignorable) {
		}
		
		int selection = Math.abs(
				editor.getCaret().getDot() - editor.getCaret().getMark()
		);

		caretInfoLabel.setText(
				String.format(
						flp.getString("line") + "%d " 
						+ flp.getString("col")+ "%d " 
						+ flp.getString("sel") + "%d",
						row, 
						col, 
						selection
				)
		);
	}

	/**
	 * Checks if there are any unsaved documents before exiting the program.
	 * Performs the check on all opened documents.
	 * 
	 * @return true if the user has aborted closing the program
	 */
	private boolean checkForUnsavedDocuments() {
		boolean closingAborted = false;
		for (SingleDocumentModel document : documents) {
			closingAborted = checkSingleUnsavedDocument(document);
			if (closingAborted) {
				break;
			}
		}
		return closingAborted;
	}

	/**
	 * Usually called when a tab, or the entire program, is closed. If
	 * <code>document</code> is not saved, then the user is prompted if they
	 * want to save the document before closing.
	 * 
	 * @param document
	 *            the document to be checked
	 * @return true if the user aborted closing, false otherwise
	 * @see SingleDocumentModel#isModified()
	 */
	private boolean checkSingleUnsavedDocument(SingleDocumentModel document) {
		boolean closingAborted = false;

		if (document != null && document.isModified()) {
			String pathName = document.getFilePath() == null ? ""
					: document.getFilePath().getFileName().toString();
			int result = JOptionPane.showConfirmDialog(
					JNotepadPP.this,
					flp.getString("save_query_start") + pathName 
					+ flp.getString("save_query_end"),
					flp.getString("save"), 
					JOptionPane.YES_NO_CANCEL_OPTION
			);

			switch (result) {
			case JOptionPane.YES_OPTION:
				saveDocument.actionPerformed(null);
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
				closingAborted = true;
				break;
			}
		}
		return closingAborted;
	}

	/**
	 * Initializes all actions which can be started using a keyboard shortcut.
	 */
	private void initActions() {

		openDocument.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
		openDocument.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		
		saveDocument.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
		saveDocument.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);

		exit.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
		exit.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);

		newBlankDocument.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		newBlankDocument.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

		saveDocumentAs.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
		saveDocumentAs.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);

		closeCurrentDocument.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
		closeCurrentDocument.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);

		statisticalInfo.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control I"));
		statisticalInfo.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);

		LanguageDE.putValue(Action.NAME, "Deutsch");
		LanguageEN.putValue(Action.NAME, "English");
		LanguageHR.putValue(Action.NAME, "Hrvatski");
	}

	/**
	 * This listener makes sure that the document editing menus are disabled
	 * when there is no available document for editing.
	 */
	private final MultipleDocumentListener toggleListener = new MultipleDocumentListener() {

		@Override
		public void documentRemoved(SingleDocumentModel model) {
			toggleEnabled();
		}

		@Override
		public void documentAdded(SingleDocumentModel model) {
			toggleEnabled();
		}

		@Override
		public void currentDocumentChanged(SingleDocumentModel previousModel,
				SingleDocumentModel currentModel) {
			toggleEnabled();
		}

	};

	/**
	 * Used in the <code>toggleListener</code>, toggles set menus and actions
	 * depending on whether there are any documents or not.
	 */
	private void toggleEnabled() {
		boolean flag = documents.getNumberOfDocuments() != 0;

		sortMenu.setEnabled(flag);
		editMenu.setEnabled(flag);
		toolsMenu.setEnabled(flag);
		saveDocument.setEnabled(flag);
		saveDocumentAs.setEnabled(flag);
	}

	/**
	 * Adds all of the {@link JMenu}s to the {@link JMenuBar} of the program.
	 */
	private void addMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new LJMenu("file", flp);
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(newBlankDocument));
		fileMenu.add(new JMenuItem(openDocument));
		fileMenu.add(new JMenuItem(saveDocument));
		fileMenu.add(new JMenuItem(saveDocumentAs));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(closeCurrentDocument));
		fileMenu.add(new JMenuItem(statisticalInfo));
		fileMenu.add(new JMenuItem(exit));

		editMenu = new LJMenu("edit", flp);
		menuBar.add(editMenu);
		editMenu.add(new JMenuItem(copyAction));
		editMenu.add(new JMenuItem(pasteAction));
		editMenu.add(new JMenuItem(cutAction));
		editMenu.add(new JMenuItem(uniqueAction));

		JMenu languageMenu = new LJMenu("languages", flp);
		menuBar.add(languageMenu);

		languageMenu.add(new JMenuItem(LanguageEN));
		languageMenu.add(new JMenuItem(LanguageHR));
		languageMenu.add(new JMenuItem(LanguageDE));

		toolsMenu = new LJMenu("tools", flp);
		menuBar.add(toolsMenu);

		toolsMenu.add(new JMenuItem(toUpperCaseAction));
		toolsMenu.add(new JMenuItem(invertCaseAction));
		toolsMenu.add(new JMenuItem(toLowerCaseAction));
		toolsMenu.setEnabled(false);

		sortMenu = new LJMenu("sort", flp);
		menuBar.add(sortMenu);

		sortMenu.add(new JMenuItem(sortAscendingAction));
		sortMenu.add(new JMenuItem(sortDescendingAction));

		toggleEnabled();
		documents.addMultipleDocumentListener(toggleListener);

		this.setJMenuBar(menuBar);
	}

	/**
	 * Adds the toolbar and all of its elements.
	 */
	private void addToolbar() {
		JToolBar toolBar = new LJToolBar("tools", flp);
		toolBar.setFloatable(true);

		toolBar.add(new JButton(newBlankDocument));
		toolBar.add(new JButton(openDocument));
		toolBar.add(new JButton(saveDocumentAs));
		toolBar.add(new JButton(saveDocument));
		toolBar.addSeparator();
		toolBar.add(new JButton(exit));
		toolBar.add(new JButton(closeCurrentDocument));
		toolBar.addSeparator();
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.add(new JButton(cutAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(statisticalInfo));

		getContentPane().add(toolBar, BorderLayout.PAGE_START);

	}

	/**
	 * Sorts the selected elements in the text, in ascending order.
	 */
	private final Action sortAscendingAction = new LocalizableAction("sort_asc",
			flp) {

		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			performAction(
					col -> col.stream().
					sorted(getLocaleComparator())
					.collect(Collectors.toList())
			);
		}
	};

	/**
	 * Sorts the selected elements in the text, in descending order.
	 */
	private final Action sortDescendingAction = new LocalizableAction(
			"sort_desc", flp) {

		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			performAction(
						col -> col.stream().
						sorted(getLocaleComparator().reversed())
						.collect(Collectors.toList())
			);
		}
	};

	/**
	 * Removes from selection all lines which are duplicates (only the first
	 * occurrence is retained).
	 */
	private final Action uniqueAction = new LocalizableAction("unique", flp) {
		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			performAction(
					col -> col.stream()
					.collect(Collectors.toCollection(LinkedHashSet::new))
			);
		}
	};

	/**
	 * Performs the reduntant action used in the sorting/unique methods.
	 * 
	 * @param func
	 *            the function which will be applied to the selected lines
	 */
	private void performAction(
			Function<Collection<String>, Collection<String>> func) {
				
		try {
			//calculate the start and end index for text replacement
			int selStartingLine = editor.getLineOfOffset(
					editor.getSelectionStart());
			int selEndingLine = editor.getLineOfOffset(
					editor.getSelectionEnd());

			int start = editor.getLineStartOffset(selStartingLine);
			int end = editor.getLineEndOffset(selEndingLine);

			/*get all lines from the selected text (include entire lines, 
			  regardless of whether the selection was at the start or in 
			  the middle of the line */
			String str = editor.getDocument().getText(start,end - start) + '\n';
			Collection<String> selectedLines = Arrays.asList(str.split("\\n"));
			
			//apply the function to the collection of selected lines
			selectedLines = func.apply(selectedLines);
			
			//remove old text
			editor.getDocument().remove(start, end - start);
			
			StringBuilder sb = new StringBuilder();
			selectedLines.forEach(line -> sb.append(line).append('\n'));
			
			//add new text
			editor.getDocument().insertString(start, sb.toString(), null);
		} catch (BadLocationException ignorable) {
		}
	}

	/**
	 * @return the String comparator depending on the locale set
	 */
	private Comparator<String> getLocaleComparator() {
		Locale locale = new Locale(flp.getString("current_lang"));

		return (a, b) -> Collator.getInstance(locale).compare(a, b);
	}

	/**
	 * Copies selected text to clipboard.
	 */
	private final Action copyAction = new LocalizableAction("copy", flp) {

		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new DefaultEditorKit.CopyAction().actionPerformed(arg0);

		}
	};

	/**
	 * Cuts selected text to clipboard.
	 */
	private final Action cutAction = new LocalizableAction("cut", flp) {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new DefaultEditorKit.PasteAction().actionPerformed(arg0);

		}
	};

	/**
	 * Pastes text from clipboard.
	 */
	private final Action pasteAction = new LocalizableAction("paste", flp) {
		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new DefaultEditorKit.CutAction().actionPerformed(arg0);
		}
	};

	/**
	 * Shows statistical info about the current document.
	 */
	private final Action statisticalInfo = new LocalizableAction("stats", flp) {

		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (documents.getCurrentDocument() == null) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("no_file_found"),
						flp.getString("stats_title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String text = documents.getCurrentDocument().getTextComponent()
					.getText();

			int numOfCharacters = text.length();

			int numOfNonBlankCharacters = text.replaceAll("\\s+", "").length();

			LineNumberReader lineNumberReader = new LineNumberReader(
					new StringReader(text));
			try {
				lineNumberReader.skip(Long.MAX_VALUE);
			} catch (IOException ignorable) {
			}

			int numOfLines = lineNumberReader.getLineNumber() + 1;

			//show stats info in a JOptionPane
			JOptionPane.showMessageDialog(JNotepadPP.this,
					flp.getString("stats1") + numOfCharacters
							+ flp.getString("stats2") + numOfNonBlankCharacters
							+ flp.getString("stats3") + numOfLines
							+ flp.getString("stats4"),
					flp.getString("stats_title"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * Used to open existing file from disk.
	 */
	private final Action openDocument = new LocalizableAction("open_document",
			flp) {

		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(flp.getString("open_file"));

			if (jfc.showOpenDialog(
					JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File filename = jfc.getSelectedFile();
			Path filepath = filename.toPath();

			if (!Files.isReadable(filepath)) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("file_err1") + filename.getAbsolutePath()
								+ " " + flp.getString("file_err2"),
						flp.getString("error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			documents.loadDocument(filepath);
		}
	};

	/**
	 * Used to save current file to disk.
	 */
	private final Action saveDocument = new LocalizableAction("save", flp) {

		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Path newPath = null;

			if (documents.getCurrentDocument().getFilePath() == null) {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle(flp.getString("save"));
				if (jfc.showSaveDialog(
						JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(JNotepadPP.this,
							flp.getString("nothing_saved"),
							flp.getString("warning"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				newPath = jfc.getSelectedFile().toPath();
				documents.getCurrentDocument()
						.setFilePath(jfc.getSelectedFile().toPath());
			}

			newPath = newPath == null
					? documents.getCurrentDocument().getFilePath()
					: newPath;
			SingleDocumentModel newModel = new DefaultSingleDocumentModel(
					newPath, documents.getCurrentDocument().getTextComponent()
							.getText());
			documents.saveDocument(newModel, newPath);
		}
	};

	/**
	 * Used to save current file to disk to defined path.
	 */
	private final Action saveDocumentAs = new LocalizableAction("save_as",
			flp) {

		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(flp.getString("save_as"));
			if (jfc.showSaveDialog(
					JNotepadPP.this) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(JNotepadPP.this,
						flp.getString("nothing_saved"),
						flp.getString("warning"), JOptionPane.WARNING_MESSAGE);
				return;
			}
			Path newPath = jfc.getSelectedFile().toPath();
			documents.getCurrentDocument().setFilePath(newPath);

			SingleDocumentModel newModel = new DefaultSingleDocumentModel(
					newPath, documents.getCurrentDocument().getTextComponent()
							.getText());
			documents.saveDocument(newModel, newPath);

		}
	};

	/**
	 * Creates new blank file.
	 */
	private final Action newBlankDocument = new LocalizableAction("new", flp) {

		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			documents.createNewDocument();
		}
	};

	/**
	 * Closes current tab.
	 */
	private final Action closeCurrentDocument = new LocalizableAction("close",
			flp) {

		/**
		 * Default UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			SingleDocumentModel document = documents.getCurrentDocument();
			boolean closingAborted = checkSingleUnsavedDocument(document);
			if (!closingAborted) {
				documents.closeDocument(documents.getCurrentDocument());
			}
		}
	};

	/**
	 * Exits the application.
	 */
	private final Action exit = new LocalizableAction("exit", flp) {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean closingAborted = checkForUnsavedDocuments();
			if (!closingAborted) {
				dispose();
				System.exit(0);
			}
		}

	};

	/**
	 * Inverts the cases for the selected part of text - uppercase letters
	 * become lowercase, and vice versa.
	 */
	private final Action invertCaseAction = new LocalizableAction("invert_case",
			flp) {

		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			modifyText(invertCase);
		}
	};

	/**
	 * Turns the entire selected text to lowercase letters.
	 */
	private final Action toLowerCaseAction = new LocalizableAction(
			"to_lowercase", flp) {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			modifyText(String::toLowerCase);
		}
	};

	/**
	 * Turns the entire selected text to uppercase letters.
	 */
	private final Action toUpperCaseAction = new LocalizableAction(
			"to_uppercase", flp) {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			modifyText(String::toUpperCase);
		}
	};

	/**
	 * Unary operator used for inverting cases of the given string.
	 */
	private UnaryOperator<String> invertCase = text -> {
		char[] chars = text.toCharArray();
		for (int i = 0, n = chars.length; i < n; i++) {
			char c = chars[i];
			chars[i] = Character.isLowerCase(c) ? Character.toUpperCase(c)
					: Character.toLowerCase(c);
		}
		return new String(chars);
	};

	/**
	 * Modifies the selected text using the given {@link UnaryOperator}.
	 * 
	 * @param operator
	 *            the operator used for modification.
	 */
	private void modifyText(UnaryOperator<String> operator) {
		Document doc = editor.getDocument();

		int len = Math
				.abs(editor.getCaret().getDot() - editor.getCaret().getMark());

		int offset = 0;

		if (len != 0) {
			offset = Math.min(editor.getCaret().getMark(),
					editor.getCaret().getDot());
		}

		try {
			String text = doc.getText(offset, len);

			text = operator.apply(text);

			doc.remove(offset, len);
			doc.insertString(offset, text, null);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Changes the program's localization to Croatian.
	 */
	private final Action LanguageHR = new AbstractAction() {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage(HRVATSKI);
		}
	};

	/**
	 * Changes the program's localization to Croatian.
	 */
	private final Action LanguageEN = new AbstractAction() {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage(ENGLISH);
		}
	};

	/**
	 * Changes the program's localization to Croatian.
	 */
	private final Action LanguageDE = new AbstractAction() {
		/** Default UID */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LocalizationProvider.getInstance().setLanguage(DEUTSCH);
		}
	};

	/**
	 * Main method. Invokes the EDT, which initializes the {@link JNotepadPP}'s
	 * frame.
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new JNotepadPP());
	}
}
