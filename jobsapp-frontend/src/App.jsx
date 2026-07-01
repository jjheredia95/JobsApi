import { Link, Route, Routes } from 'react-router-dom'
import Home from './pages/Home'
import CategoriesList from './pages/categories/CategoriesList.jsx'
import CategoryForm from "./pages/categories/CategoryForm.jsx";
import VacancyList from './pages/vacancies/vacancyList.jsx'
import VacancyForm from './pages/vacancies/vacancyForm.jsx'

export default function App() {
    return (
        <>
            <header>
                <nav className="navbar navbar-expand-md navbar-dark fixed-top bg-dark">
                    <div className="container">
                        <Link className="navbar-brand" to="/">
                            JobsHub
                        </Link>
                        <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                            <ul className="navbar-nav me-auto">
                                <li className="nav-item">
                                    <Link className="nav-link" to="/">
                                        Home
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/categories">
                                        Categories
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/vacancies">
                                        Vacancies
                                    </Link>
                                </li>
                            </ul>

                            <div className="d-flex gap-2">
                                <a className="btn btn-primary" href="#">
                                    Login
                                </a>
                                <a className="btn btn-primary" href="#">
                                    Sign Up
                                </a>
                            </div>
                        </div>
                    </div>
                </nav>
            </header>

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/categories" element={<CategoriesList />} />
                <Route path="/categories/update/:id" element={<CategoryForm />} />
                <Route path="/categories/create" element={<CategoryForm />} />
                <Route path="/vacancies" element={<VacancyList />} />
                <Route path="/vacancies/create" element={<VacancyForm />} />
                <Route path="/vacancies/update/:id" element={<VacancyForm />} />
            </Routes>

            <footer className="footer mt-auto py-3 bg-light">
                <div className="container">
                    <p className="mb-0">
                        &copy; 2026 JobsHub, Inc. | WebApp created with Spring Boot 4.0.3 | Autor: Juan R. Heredia
                        | &middot; <a href="#">Privacy</a> &middot; <a href="#">Terms</a>
                    </p>
                </div>
            </footer>
        </>
    )
}