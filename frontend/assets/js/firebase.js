// Firebase SDK
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getAuth, signInWithPopup, GoogleAuthProvider, signOut } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-auth.js";

// Firebase config
const firebaseConfig = {
  apiKey: "AIzaSyAYWRm5lCwuwAsvc0AlAIWPxfDJXwwHQAA",
  authDomain: "datvexemphim-675a1.firebaseapp.com",
  projectId: "datvexemphim-675a1",
  storageBucket: "datvexemphim-675a1.firebasestorage.app",
  messagingSenderId: "970758316041",
  appId: "1:970758316041:web:3729f5c2932f85e00b58d8"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

// Đăng nhập bằng Google
export async function signInWithGoogle() {
  try {
    const result = await signInWithPopup(auth, googleProvider);
    const user = result.user;

    // Lấy ID token
    const idToken = await user.getIdToken();

    return {
      success: true,
      user: {
        uid: user.uid,
        email: user.email,
        displayName: user.displayName,
        photoURL: user.photoURL
      },
      idToken: idToken
    };
  } catch (error) {
    console.error("Google Sign-In Error:", error);
    return {
      success: false,
      error: error.message
    };
  }
}

// Đăng xuất
export async function signOutGoogle() {
  try {
    await signOut(auth);
    return { success: true };
  } catch (error) {
    console.error("Sign Out Error:", error);
    return { success: false, error: error.message };
  }
}

// Kiểm tra đăng nhập
export function onAuthStateChange(callback) {
  auth.onAuthStateChanged(callback);
}
