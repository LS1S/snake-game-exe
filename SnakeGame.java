import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        // Настройка окна игры
        add(new GamePanel());
        setTitle("Змейка на Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack(); // Подгоняет размер окна под размер панели
        setLocationRelativeTo(null); // Центрирует окно на экране
        setVisible(true);
    }

    public static void main(String[] args) {
        // Запуск игры в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

class GamePanel extends JPanel implements ActionListener {

    // Размеры игрового поля и ячеек
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;
    private final int DOT_SIZE = 25; // Размер одной ячейки (змейки и фрукта)
    private final int ALL_DOTS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int DELAY = 140; // Скорость игры (чем меньше, тем быстрее)

    // Массивы для хранения координат всех частей тела змейки
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int bodyParts = 3; // Начальная длина змейки
    private int fruitsEaten = 0; // Набранные очки

    // Координаты фрукта
    private int fruitX;
    private int fruitY;

    // Направление движения (на старте змейка ползет вправо)
    private char direction = 'R'; 
    private boolean running = false;
    private Timer timer;
    private Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    // Инициализация игры
    public void startGame() {
        // Спавним змейку в центре/левой части поля
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * DOT_SIZE;
            y[i] = 100;
        }
        newFruit(); // Создаем первый фрукт
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Отрисовка графики
    public void draw(Graphics g) {
        if (running) {
            // Рисуем фрукт (красный круг)
            g.setColor(Color.RED);
            g.fillOval(fruitX, fruitY, DOT_SIZE, DOT_SIZE);

            // Рисуем змейку
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN); // Голова — ярко-зеленая
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Тело — темно-зеленое
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                }
            }

            // Рисуем текущий счет вверху экрана
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Очки: " + fruitsEaten, (SCREEN_WIDTH - metrics.stringWidth("Очки: " + fruitsEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    // Генерация нового фрукта в случайном месте поля
    public void newFruit() {
        fruitX = random.nextInt((int) (SCREEN_WIDTH / DOT_SIZE)) * DOT_SIZE;
        fruitY = random.nextInt((int) (SCREEN_HEIGHT / DOT_SIZE)) * DOT_SIZE;
    }

    // Логика движения змейки
    public void move() {
        // Сдвигаем координаты тела вслед за головой
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Меняем координату головы в зависимости от направления
        switch (direction) {
            case 'U': y[0] = y[0] - DOT_SIZE; break; // Вверх
            case 'D': y[0] = y[0] + DOT_SIZE; break; // Вниз
            case 'L': x[0] = x[0] - DOT_SIZE; break; // Влево
            case 'R': x[0] = x[0] + DOT_SIZE; break; // Вправо
        }
    }

    // Проверка, съела ли змейка фрукт
    public void checkFruit() {
        if ((x[0] == fruitX) && (y[0] == fruitY)) {
            bodyParts++;   // Увеличиваем змейку
            fruitsEaten++; // +1 очко
            newFruit();    // Спавним новый фрукт
        }
    }

    // Проверка столкновений (проигрыш)
    public void checkCollisions() {
        // Проверка: врезалась ли голова в собственное тело
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Проверка: вышла ли голова за границы поля
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    // Экран окончания игры
    public void gameOver(Graphics g) {
        // Текст "Игра окончена"
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Игра окончена", (SCREEN_WIDTH - metrics1.stringWidth("Игра окончена")) / 2, SCREEN_HEIGHT / 2);

        // Итоговый счет
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Ваш результат: " + fruitsEaten, (SCREEN_WIDTH - metrics2.stringWidth("Ваш результат: " + fruitsEaten)) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFruit();
            checkCollisions();
        }
        repaint(); // Перерисовываем экран на каждом шаге таймера
    }

    // Обработчик нажатий клавиш (стрелочки)
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') { // Нельзя мгновенно развернуться в противоположную сторону
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
