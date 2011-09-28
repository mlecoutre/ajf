package ajf.configuration;

import java.util.StringTokenizer;

import org.apache.commons.beanutils.expression.Resolver;

public class ELStyleResolver implements Resolver {

	private static final char NESTED = '.';
	private static final char MAPPED_START = '[';
	private static final char MAPPED_END = ']';
	private static final char INDEXED_START = '(';
	private static final char INDEXED_END = ')';
	
	private static final String DELIMITERS = String.valueOf(NESTED).concat(String.valueOf(MAPPED_START)).concat(String.valueOf(INDEXED_START));
		

	public ELStyleResolver() {
		super();
	}
	
	@Override
	public int getIndex(String expression) {
		if (expression == null || expression.length() == 0) {
            return -1;
        }
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == NESTED || c == MAPPED_START) {
                return -1;
            } else if (c == INDEXED_START) {
                int end = expression.indexOf(INDEXED_END, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                String value = expression.substring(i + 1, end);
                if (value.length() == 0) {
                    throw new IllegalArgumentException("No Index Value");
                }
                int index = 0;
                try {
                    index = Integer.parseInt(value, 10);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid index value '"
                            + value + "'");
                }
                return index;
            }
        }
        return -1;
	}

	@Override
	public String getKey(String expression) {
		if (expression == null || expression.length() == 0) {
            return null;
        }
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == NESTED || c == INDEXED_START) {
                return null;
            } else if (c == MAPPED_START) {
                int end = expression.indexOf(MAPPED_END, i);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing End Delimiter");
                }
                return expression.substring(i + 1, end);
            }
        }
        return null;
	}

	@Override
	public String getProperty(String expression) {
		 if (expression == null || expression.length() == 0) {
	            return expression;
	        }
	        for (int i = 0; i < expression.length(); i++) {
	            char c = expression.charAt(i);
	            if (c == NESTED || c == MAPPED_START || c == INDEXED_START) {
	                return expression.substring(0, i);
	            }
	        }
	        return expression;
	}

	@Override
	public boolean hasNested(String expression) {
		if (expression == null || expression.length() == 0) {
            return false;
        } else {
            return (remove(expression) != null);
        }
	}

	@Override
	public boolean isIndexed(String expression) {
		if (expression == null || expression.length() == 0) {
			return false;
		}
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == NESTED || c == MAPPED_START) {
				return false;
			}
			else
				if (c == INDEXED_START) {
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean isMapped(String expression) {
		if (expression == null || expression.length() == 0) {
			return false;
		}
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == NESTED || c == INDEXED_START) {
				return false;
			}
			else
				if (c == MAPPED_START) {
					return true;
				}
		}
		return false;
	}

	@Override
	public String next(String expression) {
		if (expression == null || expression.length() == 0) {
			return null;
		}
		boolean indexed = false;
		boolean mapped = false;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (indexed) {
				if (c == INDEXED_END) {
					return expression.substring(0, i + 1);
				}
			}
			else
				if (mapped) {
					if (c == MAPPED_END) {
						return expression.substring(0, i + 1);
					}
				}
				else {
					if (c == NESTED) {
						return expression.substring(0, i);
					}
					else
						if (c == MAPPED_START) {
							mapped = true;
						}
						else
							if (c == INDEXED_START) {
								indexed = true;
							}
				}
		}
		return expression;
	}
	
	/**
	 * 
	 * @param expression
	 * @return
	 */
	public String first(String expression) {
		
		if (expression == null || expression.length() == 0) 
			return null;
		
		StringTokenizer tokenizer = new StringTokenizer(expression, DELIMITERS);
		String firstKey = tokenizer.nextToken();
		return firstKey;
	}

	@Override
	public String remove(String expression) {
		if (expression == null || expression.length() == 0) {
			return null;
		}
		String property = next(expression);
		if (expression.length() == property.length()) {
			return null;
		}
		int start = property.length();
		if (expression.charAt(start) == NESTED) {
			start++;
		}
		return expression.substring(start);
	}

}
