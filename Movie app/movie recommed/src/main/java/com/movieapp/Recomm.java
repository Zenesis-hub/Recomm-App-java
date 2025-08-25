package com.movieapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Recomm - Fascinating Movie Discovery App
 * A Netflix-style app with modern UI, animations, and responsive design
 * 
 * @author Human Developer
 * @version 2.1 - Fixed Posters & Centered Layout
 */
public class Recomm {
    
    // Updated API configuration with better error handling
    private static final String API_KEY = "1ad4c29c"; // Your current API key
    private static final String OMDB_URL = "https://www.omdbapi.com/"; // Use HTTPS for better reliability

    // Fallback poster URLs for popular movies (in case API fails)
    private static final Map<String, String> FALLBACK_POSTERS = new HashMap<>();
    static {
        FALLBACK_POSTERS.put("The Dark Knight", "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Inception", "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Pulp Fiction", "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("The Godfather", "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3YTFkXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Interstellar", "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGE2LWE5ODgtYzM4MzJjMzI2ZWM1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Breaking Bad", "https://m.media-amazon.com/images/M/MV5BMjhiMzgxZTctNDc1Ni00OTIxLTlhMTYtZTA3ZWFkODRkNmE2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Friends", "https://m.media-amazon.com/images/M/MV5BNDVkYjU0MzctMWRmZi00NTkxLTgwZWEtOWVhYjZlYjFmZGUzXkEyXkFqcGdeQXVyNTA4NzY1MzY@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Game of Thrones", "https://m.media-amazon.com/images/M/MV5BYTRiNDQwYzAtMzVlZS00NTI5LWJjYjUtMzkwNTUzMWMxZTllXkEyXkFqcGdeQXVyNDIzMzcwNjc@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Death Note", "https://m.media-amazon.com/images/M/MV5BNjRiNmNjMmMtN2U2Yi00ODNjLWE4OTMtYjBkODIxN2Y4YzFmXkEyXkFqcGdeQXVyNTA4NzY1MzY@._V1_SX300.jpg");
        FALLBACK_POSTERS.put("Attack on Titan", "https://m.media-amazon.com/images/M/MV5BNzc5MTczNDQtNDFjNi00ZDU5LWFkNzItOTE1NzQzMzdhN2QzXkEyXkFqcGdeQXVyNTgyNTA4MjM@._V1_SX300.jpg");
    }
    
    // Enhanced color scheme with gradients
    private static final Color DARK_BG = new Color(15, 15, 20);
    private static final Color CARD_BG = new Color(30, 30, 40);
    private static final Color ACCENT = new Color(229, 9, 20);
    private static final Color ACCENT_GLOW = new Color(255, 50, 60);
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY = new Color(180, 180, 180);
    private static final Color DARK_ACCENT = new Color(40, 40, 60);
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RecommApp().setVisible(true);
        });

    }
    
    static class RecommApp extends JFrame {
        private JTextField searchField;
        private JButton searchButton;
        private JPanel contentPanel;
        private JScrollPane scrollPane;
        private Timer animationTimer;
        private int animationOffset = 0;
        
        public RecommApp() {
            setupWindow();
            createUI();
            showHome();
            startAnimations();
        }
        
        private void setupWindow() {
            setTitle("Recomm - Discover Amazing Content");
            setSize(1200, 800);
            setMinimumSize(new Dimension(800, 600));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(DARK_BG);
            
            // Add window listener for responsive design
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateLayout();
                }
            });
        }
        
        private void createUI() {
            // Header with gradient effect
            createAnimatedHeader();
            
            // Content area with custom scrollbar
            createContentArea();
            
            // Events
            setupEventListeners();
        }
        
        private void createAnimatedHeader() {
            JPanel header = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    
                    // Create gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, DARK_BG,
                        getWidth(), 0, DARK_ACCENT
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Add subtle pattern
                    g2d.setColor(new Color(255, 255, 255, 5));
                    for (int i = 0; i < getWidth(); i += 20) {
                        g2d.drawLine(i, 0, i, getHeight());
                    }
                    
                    g2d.dispose();
                }
            };
            header.setOpaque(false);
            header.setBorder(new EmptyBorder(25, 35, 25, 35));
            header.setPreferredSize(new Dimension(2800, 150));
            
            // Animated title with glow effect
            JLabel title = new JLabel("Recomm") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw glow effect
                    g2d.setColor(ACCENT_GLOW);
                    g2d.setFont(new Font("ARIAL", Font.BOLD, 50));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 3;
                    
                    // Multiple glow layers
                    for (int i = 0; i < 3; i++) {
                        g2d.drawString(getText(), x + i, y + i);
                    }
                    
                    // Main text
                    g2d.setColor(ACCENT);
                    g2d.drawString(getText(), x, y);

                    g2d.dispose();
                }
            };
            title.setPreferredSize(new Dimension(200, 60));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Enhanced search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            searchPanel.setOpaque(false);
            
            // Stylish search field
            searchField = new JTextField(25) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Rounded background
                    g2d.setColor(CARD_BG);
                    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 20, 20);
                    
                    // Border
                    g2d.setColor(ACCENT);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                    
                    // Text
                    g2d.setColor(WHITE);
                    g2d.setFont(getFont());
                    Insets insets = getInsets();
                    g2d.drawString(getText(), insets.left + 5, getHeight() - insets.bottom - 5);
                    
                    g2d.dispose();
                }
            };
            searchField.setFont(new Font("Arial", Font.PLAIN, 16));
            searchField.setForeground(WHITE);
            searchField.setCaretColor(WHITE);
            searchField.setBorder(BorderFactory.createEmptyBorder(12, 17, 12, 15));
            searchField.setOpaque(false);
            searchField.setPreferredSize(new Dimension(200, 45));
            
            // Glowing search button
            searchButton = new JButton("🔍 Search") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Button glow
                    g2d.setColor(ACCENT_GLOW);
                    g2d.fillRoundRect(-2, 2, getWidth() - 4, getHeight() - 3, 25, 25);
                    
                    // Main button
                    g2d.setColor(ACCENT);
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 25, 25);
                    
                    // Text
                    g2d.setColor(WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                    
                    g2d.dispose();
                }
            };
            searchButton.setPreferredSize(new Dimension(120, 45));
            searchButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            searchButton.setFocusPainted(false);
            searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            
            header.add(title, BorderLayout.CENTER);
            header.add(searchPanel, BorderLayout.EAST);
            
            add(header, BorderLayout.NORTH);
        }
        
        private void createContentArea() {
            contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(DARK_BG);
            
            scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBackground(DARK_BG);
            scrollPane.getViewport().setBackground(DARK_BG);
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            
            // Custom scrollbar
            scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
            
            add(scrollPane, BorderLayout.CENTER);
        }
        
        private void setupEventListeners() {
            searchButton.addActionListener(e -> search());
            searchField.addActionListener(e -> search());
            
            // Add hover effects to search button
            searchButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            });
        }
        
        private void startAnimations() {
            animationTimer = new Timer(50, e -> {
                animationOffset = (animationOffset + 1) % 360;
                repaint();
            });
            animationTimer.start();
        }
        
        private void updateLayout() {
            // Responsive layout adjustments
            int width = getWidth();
            int height = getHeight();
            
            // Center content based on window size
            if (width < 1000) {
                // Compact mode - smaller margins
                contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
            } else if (width < 1400) {
                // Medium mode - balanced margins
                contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
            } else {
                // Large mode - center content with calculated margins
                int margin = Math.max(50, (width - 1200) / 2);
                contentPanel.setBorder(new EmptyBorder(30, margin, 30, margin));
            }
            
            contentPanel.revalidate();
            contentPanel.repaint();
        }
        
        private void showHome() {
            contentPanel.removeAll();
            
            // Animated welcome section
            createAnimatedWelcome();
            
            // Enhanced content sections
            addEnhancedSection("🔥 Trending Movies", Arrays.asList(
                "The Dark Knight", "Inception", "Pulp Fiction", "The Godfather", "Interstellar"
            ));
            
            addEnhancedSection("📺 Popular TV Shows", Arrays.asList(
                "Breaking Bad", "Friends", "Game of Thrones", "Stranger Things", "The Office"
            ));
            
            addEnhancedSection("🎌 Amazing Anime", Arrays.asList(
                "Death Note", "Attack on Titan", "One Piece", "Naruto", "Demon Slayer"
            ));
            
            addEnhancedSection("⭐ New Releases", Arrays.asList(
                "Dune", "The Batman", "Top Gun: Maverick", "Everything Everywhere", "Nope"
            ));
            
            contentPanel.revalidate();
            contentPanel.repaint();
        }
        
        private void createAnimatedWelcome() {
            JPanel welcome = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Animated gradient background
                    int time = (int) (System.currentTimeMillis() / 10) % 360;
                    Color color1 = Color.getHSBColor(time / 360f, 0.3f, 0.1f);
                    Color color2 = Color.getHSBColor((time + 180) / 360f, 0.3f, 0.15f);
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color1,
                        getWidth(), getHeight(), color2
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    
                    // Add animated particles
                    Random rand = new Random(123);
                    g2d.setColor(new Color(255, 255, 255, 30));
                    for (int i = 0; i < 20; i++) {
                        int x = (int) (rand.nextDouble() * getWidth());
                        int y = (int) (rand.nextDouble() * getHeight());
                        int size = 2 + (int) (rand.nextDouble() * 4);
                        g2d.fillOval(x, y, size, size);
                    }
                    
                    g2d.dispose();
                }
            };
            welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
            welcome.setBorder(new EmptyBorder(40, 40, 40, 40));
            welcome.setMaximumSize(new Dimension(1200, 250));
            welcome.setOpaque(false);
            
            JLabel welcomeText = new JLabel("Welcome to Recomm!");
            welcomeText.setFont(new Font("Arial Black", Font.BOLD, 36));
            welcomeText.setForeground(WHITE);
            welcomeText.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel subtitle = new JLabel("Discover your next favorite movie, TV show, or anime");
            subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
            subtitle.setForeground(GRAY);
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Animated explore button
            JButton exploreBtn = new JButton("🚀 Explore Now") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Animated glow
                    int glow = (int) (Math.sin(System.currentTimeMillis() / 200.0) * 20 + 30);
                    g2d.setColor(new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), glow));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);
                    
                    // Main button
                    g2d.setColor(ACCENT);
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 30, 30);
                    
                    // Text
                    g2d.setColor(WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 18));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                    
                    g2d.dispose();
                }
            };
            exploreBtn.setPreferredSize(new Dimension(200, 50));
            exploreBtn.setMaximumSize(new Dimension(200, 50));
            exploreBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            exploreBtn.setFocusPainted(false);
            exploreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            exploreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            welcome.add(welcomeText);
            welcome.add(Box.createVerticalStrut(20));
            welcome.add(subtitle);
            welcome.add(Box.createVerticalStrut(30));
            welcome.add(exploreBtn);
            
            contentPanel.add(welcome);
            contentPanel.add(Box.createVerticalStrut(30));
        }
        
        private void addEnhancedSection(String title, List<String> items) {
            JPanel section = new JPanel(new BorderLayout());
            section.setBackground(DARK_BG);
            section.setBorder(new EmptyBorder(0, 40, 30, 40));
            
            // Enhanced section title with icon
            JLabel sectionTitle = new JLabel(title);
            sectionTitle.setFont(new Font("Arial Black", Font.BOLD, 24));
            sectionTitle.setForeground(WHITE);
            
            // Center the cards panel
            JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            cardsPanel.setBackground(DARK_BG);
            
            for (String item : items) {
                cardsPanel.add(createEnhancedCard(item));
            }
            
            section.add(sectionTitle, BorderLayout.NORTH);
            section.add(cardsPanel, BorderLayout.CENTER);
            
            contentPanel.add(section);
        }
        
        private JPanel createEnhancedCard(String title) {
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Rounded card background
                    g2d.setColor(CARD_BG);
                    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    
                    // Subtle border
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    
                    g2d.dispose();
                }
            };
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(15, 15, 15, 15));
            card.setPreferredSize(new Dimension(180, 280));
            card.setMaximumSize(new Dimension(180, 280));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Poster label with enhanced styling
            JLabel poster = new JLabel("🎬") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Rounded poster background
                    g2d.setColor(DARK_ACCENT);
                    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    
                    // Border
                    g2d.setColor(ACCENT);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 13, 13);
                    
                    g2d.dispose();
                }
            };
            poster.setFont(new Font("Arial", Font.BOLD, 50));
            poster.setForeground(GRAY);
            poster.setAlignmentX(Component.CENTER_ALIGNMENT);
            poster.setPreferredSize(new Dimension(140, 180));
            poster.setMaximumSize(new Dimension(140, 180));
            poster.setOpaque(false);
            poster.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Title with better styling and word wrapping
            JLabel titleLabel = new JLabel("<html><div style='text-align: center; width: 150px;'>" + title + "</div></html>");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            card.add(poster);
            card.add(Box.createVerticalStrut(15));
            card.add(titleLabel);
            
            // Load poster in background
            loadPosterAsync(title, poster);
            
            // Enhanced hover effects
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    searchAndShowDetails(title);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    card.setPreferredSize(new Dimension(200, 300));
                    card.setMaximumSize(new Dimension(200, 300));
                    card.revalidate();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    card.setPreferredSize(new Dimension(180, 280));
                    card.setMaximumSize(new Dimension(180, 280));
                    card.revalidate();
                }
            });
            
            return card;
        }
        
        private void loadPosterAsync(String title, JLabel posterLabel) {
            new Thread(() -> {
                try {
                    System.out.println("Loading poster for: " + title);
                    
                    // Skip API entirely - use fallback posters directly
                    String posterUrl = FALLBACK_POSTERS.get(title);
                    
                    if (posterUrl != null) {
                        System.out.println("Loading fallback poster for: " + title);
                        
                        try {
                            URL url = new URL(posterUrl);
                            ImageIcon originalIcon = new ImageIcon(url);
                            
                            // Scale the image
                            Image scaledImage = originalIcon.getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(scaledImage);
                            
                            SwingUtilities.invokeLater(() -> {
                                posterLabel.setIcon(scaledIcon);
                                posterLabel.setText("");
                                posterLabel.setOpaque(false);
                                System.out.println("✅ Poster loaded successfully for: " + title);
                            });
                            
                        } catch (Exception imgError) {
                            System.out.println("Image loading failed for " + title + ": " + imgError.getMessage());
                            showPlaceholder(posterLabel);
                        }
                    } else {
                        System.out.println("No fallback poster for: " + title);
                        showPlaceholder(posterLabel);
                    }
                } catch (Exception e) {
                    System.out.println("Error in poster loading for " + title + ": " + e.getMessage());
                    showPlaceholder(posterLabel);
                }
            }).start();
        }
        
        private void showPlaceholder(JLabel posterLabel) {
            SwingUtilities.invokeLater(() -> {
                posterLabel.setIcon(null);
                posterLabel.setText("🎬");
                posterLabel.setFont(new Font("Arial", Font.BOLD, 50));
                posterLabel.setForeground(GRAY);
            });
        }
        
        private String getPosterUrl(String title) {
            try {
                String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
                String urlString = OMDB_URL + "?t=" + encodedTitle + "&apikey=" + API_KEY;
                
                System.out.println("API URL: " + urlString);
                
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                
                int responseCode = connection.getResponseCode();
                System.out.println("Response code: " + responseCode);
                
                if (responseCode == 200) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                    
                    System.out.println("API Response: " + jsonResponse.toString());
                    
                    if (jsonResponse.has("Poster")) {
                        String posterUrl = jsonResponse.get("Poster").getAsString();
                        System.out.println("Poster URL from API: " + posterUrl);
                        
                        if (!posterUrl.equals("N/A") && posterUrl.startsWith("http")) {
                            return posterUrl;
                        }
                    }
                    
                    if (jsonResponse.has("Response")) {
                        String response = jsonResponse.get("Response").getAsString();
                        System.out.println("API Response status: " + response);
                    }
                } else {
                    System.out.println("HTTP Error: " + responseCode);
                }
                
                connection.disconnect();
            } catch (Exception e) {
                System.out.println("Error in getPosterUrl for " + title + ": " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        
        private void search() {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                showFancyMessage("Please enter a search term!", "Search Error");
                return;
            }
            searchAndShowDetails(query);
        }
        
        private void searchAndShowDetails(String query) {
            new Thread(() -> {
                try {
                    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
                    String urlString = OMDB_URL + "?t=" + encodedQuery + "&apikey=" + API_KEY;
                    
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    
                    if (conn.getResponseCode() == 200) {
                        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                        JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
                        
                        if (response.has("Response") && response.get("Response").getAsString().equals("True")) {
                            SwingUtilities.invokeLater(() -> showEnhancedDetails(response));
                        } else {
                            SwingUtilities.invokeLater(() -> showFancyMessage("Content not found", "Search Result"));
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> showFancyMessage("Search failed", "Error"));
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> showFancyMessage("Error: " + e.getMessage(), "Error"));
                }
            }).start();
        }
        
        private void showEnhancedDetails(JsonObject data) {
            JDialog dialog = new JDialog(this, "Content Details", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 500);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(DARK_BG);
            
            // Enhanced header with gradient
            JPanel header = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, CARD_BG,
                        getWidth(), 0, DARK_ACCENT
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    
                    g2d.dispose();
                }
            };
            header.setOpaque(false);
            header.setBorder(new EmptyBorder(25, 25, 25, 25));
            
            String title = data.has("Title") ? data.get("Title").getAsString() : "Unknown";
            String year = data.has("Year") ? data.get("Year").getAsString() : "Unknown";
            String type = data.has("Type") ? data.get("Type").getAsString() : "Unknown";
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
            titleLabel.setForeground(WHITE);
            
            JLabel infoLabel = new JLabel(year + " • " + type.toUpperCase());
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            infoLabel.setForeground(GRAY);
            
            header.add(titleLabel, BorderLayout.NORTH);
            header.add(infoLabel, BorderLayout.SOUTH);
            
            // Enhanced content panel
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(DARK_BG);
            content.setBorder(new EmptyBorder(25, 25, 25, 25));
            
            // Enhanced rating display
            if (data.has("imdbRating")) {
                String rating = data.get("imdbRating").getAsString();
                JLabel ratingLabel = new JLabel("★ Rating: " + rating + "/10") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Glowing effect
                        g2d.setColor(ACCENT_GLOW);
                        g2d.setFont(new Font("Arial", Font.BOLD, 20));
                        FontMetrics fm = g2d.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent()) / 2;
                        g2d.drawString(getText(), x + 1, y + 1);
                        
                        // Main text
                        g2d.setColor(ACCENT);
                        g2d.drawString(getText(), x, y);
                        
                        g2d.dispose();
                    }
                };
                ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(ratingLabel);
                content.add(Box.createVerticalStrut(25));
            }
            
            // Enhanced plot display
            if (data.has("Plot")) {
                String plot = data.get("Plot").getAsString();
                JLabel plotLabel = new JLabel("<html><div style='text-align: center; width: 500px; line-height: 1.5;'>" + plot + "</div></html>");
                plotLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                plotLabel.setForeground(WHITE);
                plotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(plotLabel);
                content.add(Box.createVerticalStrut(25));
            }
            
            // Enhanced genre display
            if (data.has("Genre")) {
                String genre = data.get("Genre").getAsString();
                JLabel genreLabel = new JLabel("Genre: " + genre);
                genreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                genreLabel.setForeground(GRAY);
                genreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                content.add(genreLabel);
                content.add(Box.createVerticalStrut(25));
            }
            
            // Enhanced close button
            JButton closeBtn = new JButton("Close") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Button glow
                    g2d.setColor(ACCENT_GLOW);
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 25, 25);
                    
                    // Main button
                    g2d.setColor(ACCENT);
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 25, 25);
                    
                    // Text
                    g2d.setColor(WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                    
                    g2d.dispose();
                }
            };
            closeBtn.setPreferredSize(new Dimension(120, 40));
            closeBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            closeBtn.setFocusPainted(false);
            closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeBtn.addActionListener(e -> dialog.dispose());
            
            content.add(closeBtn);
            
            dialog.add(header, BorderLayout.NORTH);
            dialog.add(content, BorderLayout.CENTER);
            
            dialog.setVisible(true);
        }
        
        private void showFancyMessage(String message, String title) {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Modern scrollbar UI
    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = ACCENT;
            this.trackColor = DARK_ACCENT;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(thumbColor);
            g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            
            g2d.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(trackColor);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
            
            g2d.dispose();
        }
    }
}
