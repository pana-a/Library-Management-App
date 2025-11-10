package models.repositories;

import models.Loan;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository {
    private final File file;
    private final List<Loan> loans = new ArrayList<>();
    private int nextId = 1;

    public LoanRepository(String filePath) {
        this.file = new File(filePath);
        loadLoans();
    }

    // ---- API public ----
    public Loan addLoan(int userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        Loan l = new Loan(nextId++, userId, bookId, borrowDate, dueDate, null);
        loans.add(l);
        return l; // salvăm la închidere
    }

    public boolean update(Loan updated) {
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).getLoanId() == updated.getLoanId()) {
                loans.set(i, updated);
                return true;
            }
        }
        return false;
    }

    public Loan findById(int loanId) {
        for (Loan l : loans) if (l.getLoanId() == loanId) return l;
        return null;
    }

    public List<Loan> listAll() { return new ArrayList<>(loans); }

    public List<Loan> listActive() {
        List<Loan> out = new ArrayList<>();
        for (Loan l : loans) if (l.isActive()) out.add(l);
        return out;
    }

    public List<Loan> listByUser(int userId) {
        List<Loan> out = new ArrayList<>();
        for (Loan l : loans) if (l.getUserId() == userId) out.add(l);
        return out;
    }

    public List<Loan> listActiveByUser(int userId) {
        List<Loan> out = new ArrayList<>();
        for (Loan l : loans) if (l.getUserId() == userId && l.isActive()) out.add(l);
        return out;
    }

    public List<Loan> listByDate(LocalDate date) {
        List<Loan> out = new ArrayList<>();
        for (Loan l : loans) if (l.getBorrowDate().equals(date)) out.add(l);
        return out;
    }

    public void saveOnExit() { saveLoans(); }

    // ---- Persistență ----
    private void loadLoans() {
        loans.clear();
        nextId = 1;
        if (!file.exists()) return;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length != 6) continue;

                int id = Integer.parseInt(p[0]);
                int userId = Integer.parseInt(p[1]);
                int bookId = Integer.parseInt(p[2]);
                LocalDate borrow = LocalDate.parse(p[3]);
                LocalDate due = LocalDate.parse(p[4]);
                LocalDate ret = "-".equals(p[5]) ? null : LocalDate.parse(p[5]);

                loans.add(new Loan(id, userId, bookId, borrow, due, ret));
                if (id >= nextId) nextId = id + 1;
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea împrumuturilor: " + e.getMessage());
        }
    }

    private void saveLoans() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            for (Loan l : loans) {
                w.write(l.getLoanId() + ";" +
                        l.getUserId() + ";" +
                        l.getBookId() + ";" +
                        l.getBorrowDate() + ";" +
                        l.getDueDate() + ";" +
                        (l.getReturnDate() == null ? "-" : l.getReturnDate().toString()));
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Eroare la salvarea împrumuturilor: " + e.getMessage());
        }
    }
}
