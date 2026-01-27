# Technical Stack - Warehouse Management System

## ✅ Frontend Web (Đã thiết lập)

### Core
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| React | ^19.2.0 | Core UI library |
| Vite | ^7.2.4 | Build tool |
| TypeScript | ~5.9.3 | Type safety |

### Routing & State
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| TanStack Router | ^1.157.15 | Routing |
| TanStack Query | ^5.90.20 | Data fetching & caching |
| Zustand | ^5.0.10 | State management |

### UI & Styling
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| TailwindCSS | ^4.1.18 | CSS framework |
| shadcn/ui | Latest | UI Components (15+ components) |
| Framer Motion | ^12.29.2 | Animations |
| Lucide React | ^0.563.0 | Icons |

### Forms & Validation
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| React Hook Form | ^7.71.1 | Form handling |
| Zod | ^4.3.6 | Schema validation |
| @hookform/resolvers | ^5.2.2 | Form validation integration |

### Data Table
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| TanStack Table | ^8.21.3 | Advanced table component |

### HTTP Client
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Axios | ^1.13.3 | HTTP requests |

### Testing
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Vitest | ^4.0.18 | Unit testing |
| React Testing Library | ^16.3.2 | Component testing |
| Playwright | ^1.58.0 | E2E testing |

### shadcn/ui Components đã cài đặt
- Button, Input, Card, Form, Label, Select
- Table, Dialog, Dropdown Menu
- Sidebar, Sheet, Separator
- Sonner (Toast), Tooltip, Skeleton

### Scripts
```bash
npm run dev          # Start dev server (http://localhost:5173)
npm run build        # Build production
npm run test         # Run unit tests
npm run test:ui      # Run tests with UI
npm run test:e2e     # Run E2E tests
npm run lint         # Lint code
```

### Cấu trúc thư mục Frontend
```
frontend/
├── src/
│   ├── components/
│   │   └── ui/           # shadcn components
│   ├── config/           # App configuration
│   ├── hooks/            # Custom hooks
│   ├── lib/              # API client, utilities
│   ├── schemas/          # Zod validation schemas
│   ├── stores/           # Zustand stores
│   ├── test/             # Test setup
│   └── types/            # TypeScript types
├── e2e/                  # Playwright E2E tests
├── .env                  # Environment variables
├── vitest.config.ts      # Vitest configuration
├── playwright.config.ts  # Playwright configuration
└── vite.config.ts        # Vite configuration
```

---

## ✅ Mobile (Đã thiết lập)

### Core
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| React Native | 0.81.5 | Mobile framework |
| Expo | ~54.0.32 | Development platform |
| TypeScript | ~5.9.2 | Type safety |

### Routing & State
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Expo Router | ~6.0.22 | File-based routing |
| TanStack Query | ^5.90.20 | Data fetching & caching |
| Zustand | ^5.0.10 | State management |

### UI & Styling
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| NativeWind | latest (v4) | Tailwind CSS for React Native |
| TailwindCSS | latest | Modern CSS framework |
| React Native Safe Area | ~5.6.0 | Handle device notches |

### Storage & Native
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Expo Secure Store | ~15.0.8 | Secure token storage |
| Async Storage | 2.2.0 | General local storage |
| Expo Constants | ~18.0.13 | System constants |

### Forms & Validation
| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| React Hook Form | ^7.71.1 | Form handling |
| Zod | ^4.3.6 | Schema validation |
| @hookform/resolvers | ^5.2.2 | integration |

### Scripts
```bash
npx expo start       # Start Expo development server
npx expo start --android # Run on Android emulator
npx expo start --ios     # Run on iOS simulator
```

### Cấu trúc thư mục Mobile
```
mobile/
├── app/              # Expo Router pages
│   ├── (auth)/       # Auth screens (login)
│   ├── _layout.tsx   # Root layout
│   └── index.tsx     # Home screen
├── src/
│   ├── config/       # App configuration
│   ├── lib/          # API client (Axios)
│   ├── schemas/      # Zod validation schemas
│   ├── stores/       # Zustand stores (Auth)
│   └── types/        # TypeScript types
├── assets/           # Images & Icons
├── global.css        # Tailwind directives
└── tailwind.config.js # Tailwind configuration
```

---

## ⏳ Backend (Chưa thiết lập)
- Spring Boot
- PostgreSQL
- Redis
- Spring Security + JWT
- REST API

---

## ✅ Test Framework
| Platform | Framework | Status |
|----------|-----------|--------|
| Frontend Unit | Vitest + React Testing Library | ✅ Đã cài |
| Frontend E2E | Playwright | ✅ Đã cài |
| Backend | JUnit | ⏳ Chưa cài |

---

## Environment Variables

### Frontend (.env)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## Cập nhật lần cuối
- **Ngày**: 26/01/2026
- **Nội dung**: Thiết lập hoàn tất Frontend React và Mobile Expo với đầy đủ thư viện core.