<!-- markdownlint-disable MD033 MD041 -->
<div align="center">
  <h1>📱 Cardify - Mobile Client</h1>
  
  <p>
    <strong>A stunning, Neo-Brutalist Android application for secure, Peer-to-Peer card sharing and discount management.</strong>
  </p>

  <p>
    <a href="#-overview">Overview</a> •
    <a href="#-features">Features</a> •
    <a href="#-tech-stack">Tech Stack</a> •
    <a href="#-demo">Demo</a> •
    <a href="#-roadmap">Roadmap</a>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin" alt="Kotlin"/>
    <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-38B26A?style=for-the-badge&logo=android" alt="Jetpack Compose"/>
    <img src="https://img.shields.io/badge/Retrofit-Network-red?style=for-the-badge" alt="Retrofit"/>
    <img src="https://img.shields.io/badge/Google_ML_Kit-OCR-blue?style=for-the-badge&logo=google" alt="ML Kit"/>
    <img src="https://img.shields.io/badge/Design-Neo--Brutalism-black?style=for-the-badge" alt="Neo-Brutalism"/>
  </p>
</div>

---

## 📖 Overview

**Cardify** is a specialized platform that bridges the digital divide in modern e-commerce. Have you ever missed out on an exclusive Amazon or Flipkart discount because you didn't own the required HDFC or SBI card? Cardify solves this by safely connecting **Buyers** looking for offers with **Card Owners** who hold the right cards.

This repository contains the **Android Mobile Client** for the Cardify ecosystem. Built with a premium **Neo-Brutalism** design language, the app provides a highly fluid, responsive, and secure interface for users to browse shared cards, automatically scan their physical cards to the platform, and manage real-time rental lifecycles.

> **Note:** The core engine powering this application lives in the [Cardify Backend Repository](https://github.com/Mahir-Agarwal/Cardify).

---

## ✨ Key Features

<table>
  <tr>
    <td width="50%">
      <h3>📸 Camera OCR Integration</h3>
      <ul>
        <li><strong>Google ML Kit</strong>: Automatically scans physical credit/debit cards using the device camera.</li>
        <li><strong>Smart BIN Routing</strong>: Extracts network types (VISA, MasterCard, RuPay) and securely identifies the issuing bank.</li>
        <li><strong>Zero Privacy Leak</strong>: Raw card numbers are processed entirely on-device and are never exposed.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🎨 Neo-Brutalism Design</h3>
      <ul>
        <li><strong>Striking Visuals</strong>: Heavy black borders, stark colors, and bold typography.</li>
        <li><strong>Interactive UI</strong>: Micro-animations, responsive haptics, and offset drop-shadows natively built into Jetpack Compose.</li>
        <li><strong>Fluid Transitions</strong>: Seamless navigation between marketplace, orders, and profile.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🛒 Live Marketplace</h3>
      <ul>
        <li><strong>Dynamic Search</strong>: Filter available cards by specific banks (HDFC, ICICI, SBI) or types (Credit / Debit).</li>
        <li><strong>Real-time State</strong>: Watch exactly where your order is in the lifecycle (Requested -> Accepted -> Paid -> Completed).</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🔐 Secure Authentication</h3>
      <ul>
        <li><strong>Local Persistence</strong>: Encrypted local storage caching securely manages JWT authentication keys.</li>
        <li><strong>Interceptors</strong>: Fully automated HTTP interception dynamically attaches authorization strings.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Kotlin 1.9+ | Core logic and Android UI code. |
| **UI Framework** | Jetpack Compose | Modern declarative UI component construction. |
| **Networking** | Retrofit & OkHttp | Lightning-fast REST API communication and asynchronous coroutines. |
| **Computer Vision** | CameraX & Google ML Kit | Used for the native card-scanning engine overlay. |
| **JSON Parsing** | Gson | Serializes the complex Order and Auth schemas. |
| **Architecture** | MVVM | Clean separation of business logic and UI presentation states. |

---

## 🎥 Demo

> **Watch Cardify in action here!**

*( coming )*

---

## 🚀 Roadmap & Currently Working On

We are actively developing new tools to enhance the peer-to-peer trading experience! Features currently in active development:

- [ ] **💬 Live Chat Functionality**: Enabling direct secure messaging bridges between Buyers and Owners for context sharing.
- [ ] **🌙 True Dark Theme**: Inverting the Neo-Brutalism styles (Pitch Black / Neon accents) for a glowing, high-contrast night mode.
- [ ] **📟 OTP-Based Login**: Seamless, password-less authentication for higher security standards.
- [ ] **💰 Live Wallet System**: Tracking total commission earned for owners and total cashback actively saved for buyers.

---

<div align="center">
  <p>
    <sub> Engineered and Developed by <a href="https://github.com/Mahir-Agarwal">Mahir Agarwal</a></sub>
  </p>
</div>
